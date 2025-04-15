package com.authenhub.constant;

import java.text.SimpleDateFormat;
import java.time.format.DateTimeFormatter;

public class Constant {
    public static final DateTimeFormatter DATE_PATH = DateTimeFormatter.ofPattern("yyyy/MM/dd");
    public static final String UNDER_SCORE = "_";
    public static final String SLASH = "/";
    public static final String PERCENT = "%";
    public static final String FILE_EXPORT_EXTENSION = ".xlsx";
    public static final String ENTER = "\n";

    public static final String DATE_FORMAT_HYPHEN = "yyyy-MM-dd HH:mm:ss";
    public static final String DATE_TIME_FORMAT_STRING = "dd-MM-yyyy HH:mm:ss";
    public static final String DATE_TIME_SLASH = "dd/MM/yyyy";

    public static final String ALIGN_LEFT = "left";
    public static final String ALIGN_RIGHT = "right";
    public static final String ALIGN_CENTER = "center";
    public static final String NUMBER_MONEY_FORMAT = "#,##0_);(#,##0)";
    public static final String TIMES_NEW_ROMAN = "Times New Roman";
    public static final String SEGOE_UI = "Segoe UI";
    public static final String NUMBER_FORMAT = "0";
    public static final String SOLID = "SOLID";


    public static final SimpleDateFormat DATE_FORMAT = new SimpleDateFormat("dd/MM/yyyy");
    public static final SimpleDateFormat MONTH_FORMAT = new SimpleDateFormat("MM/yyyy");
    public static final DateTimeFormatter MONTH_FORMATTER = DateTimeFormatter.ofPattern("MM/yyyy");
}
