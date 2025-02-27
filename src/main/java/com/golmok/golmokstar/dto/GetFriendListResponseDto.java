package com.golmok.golmokstar.dto;

import com.golmok.golmokstar.entity.InterestArea;
import lombok.Getter;

import java.util.List;

@Getter
public class GetFriendListResponseDto {
    private final Long friendId;
    private final String profilePhoto;
    private final String nickname;
    private final String friendCode;
    private final Integer travelCount;
    private final List<String> interests;

    public GetFriendListResponseDto(Long friendId, String profilePhoto, String nickname, String friendCode, Integer travelCount, List<String> interests) {
        this.friendId = friendId;
        this.profilePhoto = profilePhoto;
        this.nickname = nickname;
        this.friendCode = friendCode;
        this.travelCount = travelCount;
        this.interests = interests;
    }
}
