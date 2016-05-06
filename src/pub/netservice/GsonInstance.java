package pub.netservice;

import com.google.gson.Gson;

public class GsonInstance {

	private static Gson g = new Gson();

	public static Gson getG() {
		return g;
	}

	public static void setG(Gson g) {
		GsonInstance.g = g;
	}
	
}
