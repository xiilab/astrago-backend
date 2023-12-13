package com.xiilab.modulek8s.common.vo;

import com.xiilab.modulek8s.common.enumeration.ResourceType;

public interface K8SResourceResInter {
	//자식 클래스의 ResourceType을 조회하기 위한 메소드
	ResourceType getType();
}
