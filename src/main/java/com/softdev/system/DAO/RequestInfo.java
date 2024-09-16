package com.softdev.system.DAO;

import lombok.Data;

@Data
public class RequestInfo {
    String question;
    String engine;
    String uploadFile;
    String isUseImage;
}
