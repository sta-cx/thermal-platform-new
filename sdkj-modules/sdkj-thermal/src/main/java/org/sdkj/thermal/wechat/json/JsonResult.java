package org.sdkj.thermal.wechat.json;

import java.io.Serializable;
import java.util.Map;

/**
 * @Description: Restful统一Json响应对象封装
 * @param <T>
 */
public class JsonResult<T> implements Serializable {
	private static final long serialVersionUID = 1L;
	/**
	 * 返回状态码
	 */
	private String status;

	/**
	 * 返回消息
	 */
	private String message;

	/**
	 * 返回内容
	 */
	private T data;

	/**
	 * 分页信息
	 */
	private PageInfo page;

	/**
	 * 其他内容
	 */
	private Map<String, Object> ext;

	public String getStatus() {
		return status;
	}

	public void setStatus(String status) {
		this.status = status;
	}

	public String getMessage() {
		return message;
	}

	public void setMessage(String message) {
		this.message = message;
	}

	public T getData() {
		return data;
	}

	public void setData(T data) {
		this.data = data;
	}

	public Map<String, Object> getExt() {
		return ext;
	}

	public void setExt(Map<String, Object> ext) {
		this.ext = ext;
	}

	public PageInfo getPage() {
		return page;
	}

	public void setPage(PageInfo page) {
		this.page = page;
	}

	public JsonResult() {
		this.status = HttpStatus.OK.toString();
		this.message = "SUCCESS";
	}

	public JsonResult(String status, String message) {
		this.status = status;
		this.message = message;
	}

	public JsonResult(String status, String message, T data) {
		this.status = status;
		this.message = message;
		this.data = data;
	}

	public JsonResult(String status, String message, T data, Map<String, Object> ext) {
		this.status = status;
		this.message = message;
		this.data = data;
		this.ext = ext;
	}

	public JsonResult(String status, String message, T data, PageInfo pageInfo) {
		this.status = status;
		this.message = message;
		this.data = data;
		this.page = pageInfo;
	}

	public JsonResult(String status, String message, T data, Map<String, Object> ext, PageInfo pageInfo) {
		this.status = status;
		this.message = message;
		this.data = data;
		this.ext = ext;
		this.page = pageInfo;
	}

	public JsonResult(String status, String message, T data, Long total, Integer pageNo, Integer pageSize) {
		PageInfo pageInfo = new PageInfo(total, pageNo, pageSize);
		this.status = status;
		this.message = message;
		this.data = data;
		this.page = pageInfo;
	}

	public JsonResult(String status, String message, T data, Map<String, Object> ext, Long total, Integer pageNo, Integer pageSize) {
		PageInfo pageInfo = new PageInfo(total, pageNo, pageSize);
		this.status = status;
		this.message = message;
		this.data = data;
		this.ext = ext;
		this.page = pageInfo;
	}
}
