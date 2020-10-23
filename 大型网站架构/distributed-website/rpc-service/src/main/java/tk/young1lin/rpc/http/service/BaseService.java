package tk.young1lin.rpc.http.service;

/**
 * @author young1lin
 * @version 1.0
 * @date 2020/7/28 4:23 下午
 */
public interface BaseService {
    /**
     * 提供模板方法，供子类实现
     * @param arg1 调用参数
     * @return
     */
    Object execute(String arg1);
}
