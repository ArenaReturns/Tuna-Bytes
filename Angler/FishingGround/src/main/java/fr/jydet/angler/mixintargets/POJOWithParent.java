package fr.jydet.angler.mixintargets;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.EqualsAndHashCode;
import lombok.NoArgsConstructor;

@EqualsAndHashCode(callSuper = true)
@Data
@AllArgsConstructor
@NoArgsConstructor
public class POJOWithParent extends ParentOfPOJO {
    int var1;
    SimplePOJO pojo;

    public void noopMethod() {

    }

    public void methodCallingParent() {
        super.parentNoopMethod();
    }


    public void overridenMethod() {
        super.overridenMethod();
        publicParentField++;
    }
}
