package me.young1lin.hive;

import org.apache.hadoop.hive.ql.exec.Description;
import org.apache.hadoop.hive.ql.exec.UDFArgumentException;
import org.apache.hadoop.hive.ql.exec.UDFArgumentLengthException;
import org.apache.hadoop.hive.ql.metadata.HiveException;
import org.apache.hadoop.hive.ql.parse.SemanticException;
import org.apache.hadoop.hive.ql.udf.generic.AbstractGenericUDAFResolver;
import org.apache.hadoop.hive.ql.udf.generic.GenericUDAFEvaluator;
import org.apache.hadoop.hive.ql.udf.generic.GenericUDAFParameterInfo;
import org.apache.hadoop.hive.serde2.io.DoubleWritable;
import org.apache.hadoop.hive.serde2.objectinspector.ObjectInspector;
import org.apache.hadoop.hive.serde2.objectinspector.ObjectInspectorFactory;
import org.apache.hadoop.hive.serde2.objectinspector.PrimitiveObjectInspector;
import org.apache.hadoop.hive.serde2.objectinspector.StandardListObjectInspector;
import org.apache.hadoop.hive.serde2.objectinspector.primitive.DoubleObjectInspector;
import org.apache.hadoop.hive.serde2.objectinspector.primitive.PrimitiveObjectInspectorFactory;
import org.apache.hadoop.hive.serde2.objectinspector.primitive.PrimitiveObjectInspectorUtils;
import org.apache.hadoop.hive.serde2.typeinfo.TypeInfo;

import java.util.ArrayList;
import java.util.List;

import static org.apache.hadoop.hive.serde2.objectinspector.primitive.PrimitiveObjectInspectorFactory.javaDoubleObjectInspector;

/**
 * @author young1lin
 * @version 1.0
 * @date 2020/10/30 2:52 下午
 */
@Description(name = "regression", value = "_FUNC_(double x,avg(x),double y,avg(y)) - computes the simple linear regression")
public class LinearRegressionUDAF extends AbstractGenericUDAFResolver {

    @Override
    public GenericUDAFEvaluator getEvaluator(GenericUDAFParameterInfo info) throws SemanticException {
        ObjectInspector[] inputOIs = info.getParameterObjectInspectors();
        // 参数个数校验
        if (inputOIs.length != 4) {
            throw new UDFArgumentLengthException("except 4 params,but " + inputOIs.length);
        }
        // 判断参数是否是 double 类型
        for (ObjectInspector tmp : inputOIs) {
            if (tmp.getCategory() != ObjectInspector.Category.PRIMITIVE
                    || ((PrimitiveObjectInspector) tmp).getPrimitiveCategory() != PrimitiveObjectInspector.PrimitiveCategory.DOUBLE) {
                throw new UDFArgumentException("only support double");
            }
        }

        return new LinearRegressionUDAFEvaluator();
    }

    //    二选一，就是最开始的函数入参校验的，我比较喜欢用上面那个
//    @Override
//    public GenericUDAFEvaluator getEvaluator(TypeInfo[] info) throws SemanticException {
//        return super.getEvaluator(info);
//    }

    private static class LinearRegressionUDAFEvaluator extends GenericUDAFEvaluator {
        /**
         * 仅用于 init 方法中返回类型声明
         */
        private StandardListObjectInspector javaDoubleListInspector = ObjectInspectorFactory.getStandardListObjectInspector(javaDoubleObjectInspector);

        private DoubleObjectInspector doubleObjectInspector = PrimitiveObjectInspectorFactory.writableDoubleObjectInspector;

        private DoubleObjectInspector[] originalDataOIs = new DoubleObjectInspector[4];

        /**
         * 这个函数一定要重写，如果是自定义 UDAF 函数，Mode 的几个状态一定要了解的
         *
         * @param m          状态
         * @param parameters 入参
         * @return 当前阶段该接受什么类型的 Inspector
         */
        @Override
        public ObjectInspector init(Mode m, ObjectInspector[] parameters) throws HiveException {
            super.init(m, parameters);
            ObjectInspector result;
            if (Mode.PARTIAL1.equals(m)) {
                processOriginalDataObjectInspector(parameters);
                result = javaDoubleListInspector;
            } else if (Mode.PARTIAL2.equals(m)) {
                result = javaDoubleListInspector;
            } else if (Mode.FINAL.equals(m)) {
                result = doubleObjectInspector;
            } else {
                // COMPLETE 阶段
                result = doubleObjectInspector;
            }
            return result;
        }

        private void processOriginalDataObjectInspector(ObjectInspector[] parameters) {
            originalDataOIs[0] = (DoubleObjectInspector) parameters[0];
            originalDataOIs[1] = (DoubleObjectInspector) parameters[1];
            originalDataOIs[2] = (DoubleObjectInspector) parameters[2];
            originalDataOIs[3] = (DoubleObjectInspector) parameters[3];
        }

        /**
         * 从这往下方法，依次在不同阶段执行
         *
         * @return AggregationBuffer 这个被标记弃用， 打算用  {@link AbstractAggregationBuffer}这个代替，
         * 因为内存的一些原因，未来会隐藏这个废弃的接口
         * @deprecated use {@link AbstractAggregationBuffer} instead
         *
         */
        @Override
        public AggregationBuffer getNewAggregationBuffer() {
            LinearRegressionAggregationBuffer buffer = new LinearRegressionAggregationBuffer();
            buffer.reset();
            return buffer;
        }

        @Override
        public void reset(AggregationBuffer agg) {
            LinearRegressionAggregationBuffer buffer = (LinearRegressionAggregationBuffer) agg;
            buffer.reset();
        }

        @Override
        public void iterate(AggregationBuffer agg, Object[] inputs) {
            LinearRegressionAggregationBuffer buffer = (LinearRegressionAggregationBuffer) agg;

            double x = PrimitiveObjectInspectorUtils.getDouble(inputs[0], originalDataOIs[0]);
            double xBar = PrimitiveObjectInspectorUtils.getDouble(inputs[1], originalDataOIs[1]);
            double y = PrimitiveObjectInspectorUtils.getDouble(inputs[2], originalDataOIs[2]);
            double yBar = PrimitiveObjectInspectorUtils.getDouble(inputs[3], originalDataOIs[3]);

            buffer.xBarSubReMultiplyYBarSubReSum += buffer.reminderMultiply(x, xBar, y, yBar);
            buffer.xBarSubReSquareSum += buffer.reminderSquare(x, xBar);
            buffer.yBarSubReSquareSum += buffer.reminderSquare(y, yBar);

        }

        @Override
        public Object terminatePartial(AggregationBuffer agg) {
            LinearRegressionAggregationBuffer buffer = (LinearRegressionAggregationBuffer) agg;
            ArrayList<Double> list = new ArrayList<>(3);
            list.add(buffer.xBarSubReMultiplyYBarSubReSum);
            list.add(buffer.xBarSubReSquareSum);
            list.add(buffer.yBarSubReSquareSum);
            return list;
        }

        /**
         * 合并操作
         * @param agg init 获得的 agg 对象
         * @param param 上一步返回的对象的包装对象
         */
        @Override
        public void merge(AggregationBuffer agg, Object param) {
            LinearRegressionAggregationBuffer buffer = (LinearRegressionAggregationBuffer) agg;
            // terminatePartial 返回的是 List<Double>，所以这里可以直接强转为 List，double 在 Hive 执行过程中会自动转为 DoubleWritable 类型
            List<DoubleWritable> list = (List<DoubleWritable>)param;
            buffer.xBarSubReMultiplyYBarSubReSum += list.get(0).get();
            buffer.xBarSubReSquareSum += list.get(1).get();
            buffer.yBarSubReSquareSum += list.get(2).get();
        }

        @Override
        public Object terminate(AggregationBuffer agg) throws HiveException {
            LinearRegressionAggregationBuffer buffer = (LinearRegressionAggregationBuffer) agg;
            // 就是图中所示的公式
            double value = buffer.xBarSubReMultiplyYBarSubReSum / Math.sqrt(buffer.xBarSubReSquareSum * buffer.yBarSubReSquareSum);
            return new DoubleWritable(value);
        }

        static class LinearRegressionAggregationBuffer extends AbstractAggregationBuffer {

            /**
             * x bar 指的是 x 的平均值
             * sun((x - xBar)*(y - yBar))
             */
            double xBarSubReMultiplyYBarSubReSum;

            /**
             * sum((x - xBar)^2)
             */
            double xBarSubReSquareSum;

            /**
             * sum((y - yBar)^2)
             */
            double yBarSubReSquareSum;

            double reminderMultiply(double x, double xBar, double y, double yBar) {
                return (x - xBar) * (y - yBar);
            }

            /**
             * 求两个数，一个是当前值，一个是平均值，他们相减得到的平方值
             *
             * @param v value
             * @param vBar avg(v)
             * @return (v - vBar) * (v - vBar)
             */
            double reminderSquare(double v, double vBar) {
                return (v - vBar) * (v - vBar);
            }

            void reset() {
                xBarSubReMultiplyYBarSubReSum = 0;
                xBarSubReSquareSum = 0;
                yBarSubReSquareSum = 0;
            }
        }
    }

}
