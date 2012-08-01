package commons.callbacks.files;

import commons.callbacks.metadata.ObjectCallback;

public class InheritanceTestSuperclass {

	public String publicMethod(){
		return privateMethod();
	}

	@ObjectCallback(value = InheritanceTestCallback.class)
	private String privateMethod(){
		return "gg";
	}
}
