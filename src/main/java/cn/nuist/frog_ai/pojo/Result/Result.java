package cn.nuist.frog_ai.pojo.Result;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@NoArgsConstructor
@AllArgsConstructor
public class Result {

    private Integer code;
    private String message;
    private Object data;

    public static Result success() {
        return new Result(200, "操作成功", null);
    }

    public static Result success(Object data) {
        return new Result(200, "操作成功", data);
    }

    public static Result success(Object data, String message) {
        return new Result(200, message, data);
    }

    public static Result error() {
        return new Result(500, "操作失败", null);
    }

    public static Result error(String message) {
        return new Result(500, message, null);
    }

    public static Result error(Integer code, String message) {
        return new Result(code, message, null);
    }
}