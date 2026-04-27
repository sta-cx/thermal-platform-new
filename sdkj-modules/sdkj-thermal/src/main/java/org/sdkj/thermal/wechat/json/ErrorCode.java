package org.sdkj.thermal.wechat.json;

/**
 * @Description: 错误码
 */
public enum ErrorCode {

	PARAMETER_CHECK_ERROR(400, "参数校验错误"), AUTH_VALID_ERROR(701, "用户权限不足"), UNLOGIN_ERROR(401, "用户未登录或登录状态超时失效"),

	BAD_REQUEST_ERROR(400, "Bad Request"), unauthorized_ERROR(401, "unauthorized"), Forbidden_ERROR(403, "Forbidden"), Not_Found_ERROR(404,
			"Not Found"), SYSTEM_ERROR(500, "Server Internal Error");

	// GET
	// 200（OK） - 表示已在响应中发出
	// 204（无内容） - 资源有空表示
	// 301（Moved Permanently） - 资源的URI已被更新
	// 303（See Other） - 其他（如，负载均衡）
	// 304（not modified）- 资源未更改（缓存）
	// 400 （bad request）- 指代坏请求（如，参数错误）
	// 404 （not found）- 资源不存在
	// 406 （not acceptable）- 服务端不支持所需表示
	// 500 （internal server error）- 通用错误响应
	// 503 （Service Unavailable）- 服务端当前无法处理请求
	// POST
	// 200（OK）- 如果现有资源已被更改
	// 201（created）- 如果新资源被创建
	// 202（accepted）- 已接受处理请求但尚未完成（异步处理）
	// 301（Moved Permanently）- 资源的URI被更新
	// 303（See Other）- 其他（如，负载均衡）
	// 400（bad request）- 指代坏请求
	// 404 （not found）- 资源不存在
	// 406 （not acceptable）- 服务端不支持所需表示
	// 409 （conflict）- 通用冲突
	// 412 （Precondition Failed）- 前置条件失败（如执行条件更新时的冲突）
	// 415 （unsupported media type）- 接受到的表示不受支持
	// 500 （internal server error）- 通用错误响应
	// 503 （Service Unavailable）- 服务当前无法处理请求
	// PUT
	// 200 （OK）- 如果已存在资源被更改
	// 201 （created）- 如果新资源被创建
	// 301（Moved Permanently）- 资源的URI已更改
	// 303 （See Other）- 其他（如，负载均衡）
	// 400 （bad request）- 指代坏请求
	// 404 （not found）- 资源不存在
	// 406 （not acceptable）- 服务端不支持所需表示
	// 409 （conflict）- 通用冲突
	// 412 （Precondition Failed）- 前置条件失败（如执行条件更新时的冲突）
	// 415 （unsupported media type）- 接受到的表示不受支持
	// 500 （internal server error）- 通用错误响应
	// 503 （Service Unavailable）- 服务当前无法处理请求
	// DELETE
	// 200 （OK）- 资源已被删除
	// 301 （Moved Permanently）- 资源的URI已更改
	// 303 （See Other）- 其他，如负载均衡
	// 400 （bad request）- 指代坏请求
	// 404 （not found）- 资源不存在
	// 409 （conflict）- 通用冲突
	// 500 （internal server error）- 通用错误响应
	// 503 （Service Unavailable）- 服务端当前无法处理请求

	private final Integer value;
	private final String message;

	ErrorCode(int value, String message) {
		this.value = value;
		this.message = message;
	}

	public int getValue() {
		return value;
	}

	public String getMessage() {
		return message;
	}

	@Override
	public String toString() {
		return value.toString();
	}

	public String getCode() {
		return value.toString();
	}

	public static ErrorCode getByCode(Integer value) {
		for (ErrorCode _enum : values()) {
			if (_enum.getValue() == value) {
				return _enum;
			}
		}
		return null;
	}

}
