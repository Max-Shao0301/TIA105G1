package com.enums;

public enum Status {
	
	close(0), open(1);
	
	private int number;

	Status(int number) {
		this.number = number;
	}

	public int getNumber() {
		return number;
	}

	//int 轉 Status
    public static Status intToStatus(int number) {
    	
        for (Status status : Status.values()) {
        	
            if (status.getNumber() == number) {
                return status;
            }
            
        }

        //如果他輸入錯，帶入throw
        throw new StatusErrorException("你帶入錯誤的狀態代號 :" + number);

    }
}

class StatusErrorException extends IllegalArgumentException {

	private static final long serialVersionUID = 1L;
	
    public StatusErrorException(String message) {
        super(message);
    }
}
