package com.xiilab.modulek8s.node.enumeration;

import java.util.Arrays;

import com.xiilab.modulecommon.exception.RestApiException;
import com.xiilab.modulecommon.exception.errorcode.NodeErrorCode;

import lombok.Getter;

@Getter
public enum MIGProduct {
	H100(7, 80),
	A100_40GB(7, 40),
	A100_80GB(7, 80),
	A30(4, 24);

	private final int sm;
	private final int mem;

	MIGProduct(int sm, int mem) {
		this.sm = sm;
		this.mem = mem;
	}

	/**
	 * node label의 product를 분석하여 MIG Product로 리턴하는 메소드
	 *
	 * @param product 조회할 product
	 * @return MIGProduct
	 */
	public static MIGProduct getGpuProduct(String product) {
		MIGProduct migProduct = null;
		String core;
		String[] split;

		if (product.contains("NVIDIA-")) {
			split = product.split("-");
			core = split[1];
		} else {
			split = product.split("-");
			core = split[0];
		}

		switch (core) {
			case "H100":
				migProduct = MIGProduct.H100;
				break;
			case "A100":
				String gb = Arrays.stream(split).filter(s -> s.contains("GB")).findFirst().orElseThrow(() -> new RestApiException(NodeErrorCode.GPU_PRODUCT_MEMORY_NOT_EXIST));
				if (gb.equals("40GB")) {
					migProduct = MIGProduct.A100_40GB;
				} else if (gb.equals("80GB")) {
					migProduct = MIGProduct.A100_80GB;
				}
				break;
			case "A30":
				migProduct = MIGProduct.A30;
				break;
			default:
				break;
		}

		return migProduct;
	}
}
