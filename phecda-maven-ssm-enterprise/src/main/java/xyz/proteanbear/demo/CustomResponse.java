package xyz.proteanbear.demo;

public class CustomResponse
{
    private String resultCode;
    private String message;
    private Object data;

    public CustomResponse(String resultCode, String message, Object data) {
        this.resultCode= resultCode;
        this.message = message;
        this.data = data;
    }

    public CustomResponse() {
    }

    public String getResultCode() {
        return this.resultCode;
    }

    public void setResultCode(String resultCode) {
        this.resultCode=resultCode;
    }

    public String getMessage() {
        return this.message;
    }

    public void setMessage(String message) {
        this.message = message;
    }

    public Object getData() {
        return this.data;
    }

    public void setData(Object data) {
        this.data = data;
    }
}
