package com.enums;

public enum Gender {
	
	male(1), female(2);

	private int number;

	Gender(int number) {
		this.number = number;
	}

	public int getNumber() {
		return number;
	}

	//int 轉 Gender
    public static Gender intToGender(int number) {
    	
        for (Gender gender : Gender.values()) {
        	
            if (gender.getNumber() == number) {
                return gender;
            }
            
        }

        //如果他輸入錯，帶入throw
        throw new GenderErrorException("你帶入錯誤的性別代號 :" + number);

    }
}

class GenderErrorException extends IllegalArgumentException {

	private static final long serialVersionUID = 1L;
	
    public GenderErrorException(String message) {
        super(message);
    }
}
