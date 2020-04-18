package com.example.jukebox.model;

import lombok.Builder;
import lombok.Data;

@Data
@Builder
public class TokenRequestBody {
    private String grant_type;

    private String code;

    private String redirect_uri;
}
