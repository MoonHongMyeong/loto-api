package gg.loto.global.exception;

import com.fasterxml.jackson.annotation.JsonFormat;
import lombok.Getter;

@Getter
@JsonFormat(shape = JsonFormat.Shape.OBJECT)
public enum ErrorCode {
    // common
    INTERNAL_SERVER_ERROR(500, "Internal Server Error", "알 수 없는 에러\n 관리자에게 연락해주세요."),
    INVALID_INPUT_VALUE(400, "Invalid Input Value", "잘못된 input 값 입니다."),
    INVALID_TYPE_VALUE(400, "Invalid Type Value", "잘못된 type 입니다."),
    ENTITY_NOT_FOUND(404,"Entity Not Found", "Entity 를 찾을 수 없습니다."),
    METHOD_NOT_ALLOWED(405, "Method Not Allowed", "허용되지 않은 메소드"),
    HANDLE_ACCESS_DENIED(403, "Handle Access Denied", "엑세스가 거부되었습니다."),
    // not found
    TOKEN_NOT_FOUND(404, "Token Not Found", "토큰 정보를 찾을 수 없습니다."),
    USER_NOT_FOUND(404, "User Not Found", "사용자를 찾을 수 없습니다."),
    CHARACTER_NOT_FOUND(404, "Character Not Found", "캐릭터를 찾을 수 없습니다."),
    PARTY_NOT_FOUND(404, "Party Not Found", "공유방을 찾을 수 없습니다."),
    RAID_RECORD_NOT_FOUND(404, "Raid Record Not Found", "레이드 기록을 찾을 수 없습니다."),
    // token
    EXPIRED_TOKEN(401, "Token Is Expired", "토큰이 만료되었습니다."),
    EXPIRED_REFRESH_TOKEN(401, "Refresh Token Is Expired", "다시 로그인해주세요."),
    INVALID_TOKEN(403, "Invalid Token", "잘못된 토큰입니다."),
    // open api
    INVALID_API_KEY(401, "Invalid API Key", "API 키가 유효하지 않습니다."),
    API_REQUEST_ERROR(400, "API Request Error", "API 요청 오류가 발생했습니다."),
    LOSTARK_SERVER_ERROR(503, "Lostark Server Error", "로스트아크 서버 오류가 발생했습니다."),
    // character
    EXISTS_CHARACTER(400, "Already Exists Character", "같은 캐릭터가 이미 존재합니다."),
    // party
    EXISTS_PARTY(400, "Already Exists Party", "같은 공유방이 이미 존재합니다."),
    NOT_LEADER(403, "Not Party Leader", "공유방의 리더만 할 수 있는 요청입니다."),
    NOT_EXISTS_CHARACTER(400, "Not Exists Character", "존재하지 않는 캐릭터가 포함되어 있습니다."),
    DUPLICATE_CHARACTER_JOIN(400, "Duplicate Character Join", "이미 참여한 캐릭터입니다."),
    PARTY_CAPACITY_EXCEEDED(400, "Party Capacity Exceeded", "공유방 인원 제한이 모두 차 입장할 수 없습니다."),
    NOT_PARTY_MEMBER(403, "Not Party Member", "참여한 공유방이 아닙니다."),
    TARGET_NOT_PARTY_MEMBER(400, "Target Not Party Member", "해당 유저는 공유방에 속해있지 않습니다."),
    PARTY_LEADER_MINIMUM_CHARACTER_REQUIRED(400, "Party Leader Minimum Character Required", "방장은 최소 한 캐릭터는 소유해야 합니다.\n공유방을 떠나려면 다른 사용자에게 방장을 위임해주세요."),
    CANNOT_KICK_PARTY_LEADER(400, "Cannot Kick Party Leader", "방장을 강제 퇴장시킬 수 없습니다."),
    CANNOT_DELETE_ACTIVE_PARTY(400, "Cannot Delete Active Party", "공유방에 다른 사용자가 있으면 삭제가 불가능합니다."),
    // raid
    NOT_CHARACTER_OWNER(403, "Not Character Owner", "본인이 소유한 캐릭터만 가능한 요청입니다."),
    INSUFFICIENT_ITEM_LEVEL(400, "Insufficient Item Level", "아이템 레벨이 부족합니다."),
    DUPLICATE_RAID_CHECK(400, "Duplicate Raid Check", "이미 체크된 레이드입니다."),
    INVALID_RAID_STAGE(400, "Invalid Raid Stage", "유효하지 않은 관문입니다."),
    INVALID_RAID_DIFFICULTY(400, "Invalid Raid Difficulty", "유효하지 않은 난이도입니다."),
    UNSUPPORTED_RAID_DIFFICULTY(400, "Unsupported Raid Difficulty", "해당 레이드는 선택한 난이도를 지원하지 않습니다."),
    INSUFFICIENT_ITEM_LEVEL_FOR_RAID(400, "Insufficient Item Level For Raid", "아이템 레벨이 부족합니다. 필요 레벨: %d"),
    ;

    private final int httpStatus;
    private final String code;
    private final String message;

    ErrorCode(int httpStatus, String code, String message){
        this.httpStatus = httpStatus;
        this.code = code;
        this.message = message;
    }
}
