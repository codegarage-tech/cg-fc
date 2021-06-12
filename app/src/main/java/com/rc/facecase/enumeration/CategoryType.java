package com.rc.facecase.enumeration;

/**
 * @author Md. Rashadul Alam
 * Email: rashed.droid@gmail.com
 */
public enum CategoryType {

    NONE("")
    , ADDITIONAL_TO_CATEGORY("Additional To Category")
    , PICTURE_TO_CATEGORY("Edit To Category");

    private final String categoryTypeValue;

    private CategoryType(String value) {
        categoryTypeValue = value;
    }

    public boolean equalsName(String otherName) {
        return categoryTypeValue.equals(otherName);
    }

    @Override
    public String toString() {
        return this.categoryTypeValue;
    }

    public static CategoryType getCategoryType(String value) {
        for (CategoryType mCategoryType : CategoryType.values()) {
            if (mCategoryType.toString().equalsIgnoreCase(value)) {
                return mCategoryType;
            }
        }
        return null;
    }
}