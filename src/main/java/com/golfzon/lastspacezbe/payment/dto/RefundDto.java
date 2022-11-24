package com.golfzon.lastspacezbe.payment.dto;

import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

@Getter
@Setter
@NoArgsConstructor
public class RefundDto {

	private String merchant_uid;
	private String reason;
	private int cancel_request_amount;
	private long memberId;

	public RefundDto(String merchant_uid, String reason, int cancel_request_amount, long memberId) {
		this.merchant_uid = merchant_uid;
		this.reason = reason;
		this.cancel_request_amount = cancel_request_amount;
		this.memberId = memberId;
	}

	@Override
	public String toString() {
		return "RefundDto [merchant_uid=" + merchant_uid + ", reason=" + reason + ", cancel_request_amount="
				+ cancel_request_amount + ", memberId=" + memberId + "]";
	}

}
