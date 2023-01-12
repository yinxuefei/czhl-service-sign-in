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
public class FaceSearchResp {
    private double searchScore; //= topJson.getDouble("searchScore");

    private String faceToken; //= topJson.getString("faceToken");

    private String description;// = topJson.getString("description");

    private String imageUrl; // = topJson.getString("imageUrl");

    public boolean isCorrect(double correctScore) {
        return this.searchScore >= correctScore;
    }
}
