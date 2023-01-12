package com.gdczhl.saas.megbox;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.Getter;

/**
 * @author wuyl
 * @date 2022/12/20
 */
@Getter
@Builder
@AllArgsConstructor
public class AddFaceResp {
    private String faceToken; //: "w-ZaEm58R3xiyS4O7Xq4iw=="

    private String imageId; //: "cJljdaOV8mq0ck_6ALGBSg=="

    private FaceRect rect; //: {left: 0, top: 0, right: 293, bottom: 411}

    @Data
    public static class FaceRect {
        private Integer left; //: 0
        private Integer top; //: 0
        private Integer right; //: 293
        private Integer bottom; //: 411
    }

}
