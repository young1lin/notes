package tk.young1lin.rpc.rest.http.entity;

/**
 * @author young1lin
 * @version 1.0
 * @date 2020/8/4 3:36 下午
 */
public class ServiceEntity {
    private String service;
    private String arg1;

    public String getService() {
        return service;
    }

    public void setService(String service) {
        this.service = service;
    }

    public String getArg1() {
        return arg1;
    }

    public void setArg1(String arg1) {
        this.arg1 = arg1;
    }

    public ServiceEntity() {
    }

    public ServiceEntity(String service, String arg1) {
        this.service = service;
        this.arg1 = arg1;
    }

    @Override
    public String toString() {
        return "ServiceEntity{" +
                "service='" + service + '\'' +
                ", arg1='" + arg1 + '\'' +
                '}';
    }
}
