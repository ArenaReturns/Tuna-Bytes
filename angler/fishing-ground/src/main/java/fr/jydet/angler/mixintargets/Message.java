package fr.jydet.angler.mixintargets;

import lombok.AllArgsConstructor;
import lombok.Getter;

@Getter
@AllArgsConstructor
public class Message {
    int id;
    
    @Getter
    public static class Message1 extends Message {

        long field1 = System.currentTimeMillis();


        public Message1() {
            super(30000);
        }
    }
    
    @Getter
    public static class Message2 extends Message {

        long field2 = System.currentTimeMillis();
        
        public Message2() {
            super(30001);
        }
    }
}
