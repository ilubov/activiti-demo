package cn.ilubov.comps;

import io.swagger.annotations.ApiModelProperty;

import java.io.Serializable;

/**
 * 返回对象
 *
 * @author ilubov
 * @date 2020/10/25
 */
public class R<T> implements Serializable {

    @ApiModelProperty("返回值 0成功 -1异常 其它数字是自定义异常")
    private int code;

    @ApiModelProperty("描述")
    private String desc;

    @ApiModelProperty("总页数，只有分页时用到")
    private Long total;

    @ApiModelProperty("返回值，Object或List")
    private T data;

    public R() {}

    public int code() {
        return this.code;
    }

    public R<T> code(int code) {
        this.code = code;
        return this;
    }

    public String desc() {
        return this.desc;
    }

    public R<T> desc(String desc) {
        this.desc = desc;
        return this;
    }

    public Long total() {
        return this.total;
    }

    public R<T> total(Long total) {
        this.total = total;
        return this;
    }

    public T data() {
        return this.data;
    }

    public R<T> data(T data) {
        this.data = data;
        return this;
    }

    public static <E> R<E> error() {
        return error(-1, "小主，服务器出错了");
    }

    public static <E> R<E> error(String desc) {
        return error(-1, desc);
    }

    public static <E> R<E> error(int code, String desc) {
        R r = new R();
        r.code(code).desc(desc);
        return r;
    }

    public static <E> R<E> ok(String desc) {
        R r = new R();
        r.code(0).desc(desc);
        return r;
    }

    public static <E> R<E> ok() {
        R r = new R();
        r.code(0);
        return r;
    }

    public int getCode() {
        return this.code;
    }

    public void setCode(int code) {
        this.code = code;
    }

    public String getDesc() {
        return this.desc;
    }

    public void setDesc(String desc) {
        this.desc = desc;
    }

    public Long getTotal() {
        return this.total;
    }

    public void setTotal(Long total) {
        this.total = total;
    }

    public T getData() {
        return this.data;
    }

    public void setData(T data) {
        this.data = data;
    }

    public String toString() {
        return "R{code=" + this.code + ", desc='" + this.desc + '\'' + ", total=" + this.total + ", data=" + this.data + '}';
    }
}
