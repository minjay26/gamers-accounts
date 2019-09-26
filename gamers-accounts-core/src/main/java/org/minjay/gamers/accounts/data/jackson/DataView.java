package org.minjay.gamers.accounts.data.jackson;

public final class DataView {

    public interface Basic {}
    
    public static final class Focus {
        
        public interface Login {}
        
        public interface Default extends Basic, Login {}
    }
    
    public static final class Company {
        
        public interface Detail extends Basic {}
        
        public interface Contact extends Basic {}
        
        public interface Default extends Basic, Detail, Contact {}
    }
}
