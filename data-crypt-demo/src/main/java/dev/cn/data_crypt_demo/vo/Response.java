package dev.cn.data_crypt_demo.vo;

public class Response<D> {
    
    private D data;

    public Response(D data) {
        this.data = data;
    }

    public static <D> Response<D> ok(D data) {
        return new Response<>(data);
    }

    public D getData() {
        return data;
    }
}
