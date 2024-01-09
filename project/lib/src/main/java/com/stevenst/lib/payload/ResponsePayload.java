package com.stevenst.lib.payload;

import lombok.Getter;
import lombok.Setter;
import lombok.Builder;

@Getter
@Setter
@Builder
public class ResponsePayload {
	private int status;
	private String message;
}

