package com.example.notecook.Api;

import com.google.gson.annotations.SerializedName;

import java.util.List;

public class ValidationError {
    private List<ValidationErrorItem> errors;

    public List<ValidationErrorItem> getErrors() {
        return errors;
    }

    public void setErrors(List<ValidationErrorItem> errors) {
        this.errors = errors;
    }


    public class ValidationErrorItem {
        @SerializedName("type")
        private String type;

        @SerializedName("msg")
        private String message;

        @SerializedName("path")
        private String path;

        @SerializedName("location")
        private String location;

        @SerializedName("value")
        private String value;


        public String getValue() {
            return value;
        }

        public void setValue(String value) {
            this.value = value;
        }

        public String getType() {
            return type;
        }

        public void setType(String type) {
            this.type = type;
        }

        public String getMessage() {
            return message;
        }

        public void setMessage(String message) {
            this.message = message;
        }

        public String getPath() {
            return path;
        }

        public void setPath(String path) {
            this.path = path;
        }

        public String getLocation() {
            return location;
        }

        public void setLocation(String location) {
            this.location = location;
        }
    }
}
