package com.sixcandoit.roomservice.util;

import com.sixcandoit.roomservice.entity.room.RoomEntity;
import org.springframework.stereotype.Component;

import java.util.Comparator;
import java.util.List;

@Component("cheapRoomUtil")
public class CheapRoomUtil {

    public Integer minRoomPrice(List<RoomEntity> rooms) {
        if(rooms == null || rooms.isEmpty()){
            return null;
        }
        return rooms.stream()
                .map(RoomEntity::getRoomPrice)
                .min(Comparator.naturalOrder())
                .orElse(null);
    }

}
/*
목적 : RoomEntity 객체들의 리스트에서 가장 낮은 roomPrice 값을 찾아 반환하는 것

이 클래스는 Spring 컨테이너에 “cheapRoomUtil”라는 이름의 빈(bean)으로 등록
이를 통해 다른 클래스나 Thymeleaf 템플릿 등에서 의존성 주입(Injection) 또는 SpEL을 통해 사용할 수 있다.

minRoomPrice 메서드 :
입력값 검사 :
RoomEntity 객체들의 리스트를 매개변수로 받는다.
만약 리스트가 null이거나 비어있으면, 아무런 값을 반환할 수 없으므로 null을 반환한다.

스트림 처리 :
rooms.stream()을 사용해 리스트를 스트림으로 변환한다.
.map(RoomEntity::getRoomPrice)는 각 RoomEntity 객체에서 getRoomPrice() 메서드를 호출하여 roomPrice 값을 추출한다.
이때, roomPrice 값은 보통 숫자(Integer, BigDecimal 등)여야 하며, Comparable 인터페이스를 구현해야 한다.

최솟값 계산 :
.min(Comparator.naturalOrder())는 추출한 roomPrice 값들을 오름차순 정렬한 후, 가장 낮은 값을 찾아 반환한다.
Comparator.naturalOrder()는 자연 순서(예를 들어, 숫자라면 작은 수부터 큰 수)를 기준으로 비교하는 Comparator를 제공한다.

결과 반환 :
.orElse(null)은 스트림이 비어있을 경우, 즉 최소값을 찾지 못했을 때 null을 반환하도록 한다.
그렇지 않으면, 계산된 최소 roomPrice 값을 반환한다.

Comparator : 객체들 간의 순서를 정의하고 비교할 수 있도록 도와준다.
 */