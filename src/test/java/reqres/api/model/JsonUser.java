package reqres.api.model;

import org.json.JSONObject;

public class JsonUser {

    private final JSONObject user;
    private final String name;
    private final String job;

    public JsonUser(String name, String job) {
        user = new JSONObject();
        this.name = name;
        this.job = job;
    }

    public JSONObject buildJsonObject() {
        return user.put("name", name).put("job", job);
    }
}
