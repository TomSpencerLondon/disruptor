package org.example.disruptorservice;

public class MessageEvent {
    private String message;
    private long startTime;

    public String getMessage() {
        return message;
    }

    public void setMessage(String message) {
        this.message = message;
    }


    public void setStartTime(long startTime) {
        this.startTime = startTime;
    }

    public long getStartTime() {
        return startTime;
    }


    @Override
    public String toString() {
        return "MessageEvent{" +
                "message='" + message + '\'' +
                ", startTime=" + startTime +
                '}';
    }
}

