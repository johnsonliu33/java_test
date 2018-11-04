package com.bitspace.food.util;

public class InvitationUtil {
	private static final String ALPHABET = "5WVCT9IDRKANSMF2YPB7U468X3HEJGZQ";
    private static final int BASE = ALPHABET.length();

    public static String getInviteCode(Long uid){
        return InvitationUtil.encode(uid.longValue() * 10 + uid.longValue() % 10);
    }

    public static String encode(Long num) {
        StringBuilder sb = new StringBuilder();
        while (num > 0) {
            sb.append(ALPHABET.charAt(num.intValue() % BASE));
            num /= BASE;
        }
        return sb.reverse().toString();
    }

    public static Long getUid(String inviteCode) {
        if(inviteCode == null) return  null;
        Long referrerUid = null;
        int _referrerId = InvitationUtil.decode(inviteCode);
        int last = _referrerId % 10;
        if (last != (_referrerId / 10) % 10) {
            referrerUid = null;
        } else {
            referrerUid = (long) _referrerId / 10;
        }
        return referrerUid;
    }

    public static int decode(String str) {
        str = str.toUpperCase();
        int num = 0;
        for (int i = 0, len = str.length(); i < len; i++) {
            num = num * BASE + ALPHABET.indexOf(str.charAt(i));
        }
        return num;
    }
    
   /* public static void main(String[] args) {
        System.out.println(InvitationUtil.getInviteCode(100009L));
        System.out.println(InvitationUtil.getUid("ZY4C"));
    }*/
}
