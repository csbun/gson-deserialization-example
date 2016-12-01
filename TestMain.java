import com.google.gson.*;


import com.google.gson.annotations.Expose;
import com.google.gson.annotations.SerializedName;
import com.google.gson.reflect.TypeToken;

import java.lang.reflect.Type;
import java.util.Date;
import java.util.List;
import java.lang.Override;

/**
* 打印main方法中的输入参数
*/
public class TestMain {

    private static int uuid = 0;

    private static String getJsonStr() {
        String json = "{\n" +
                "  \"name\": \"Hans Chan " + (++uuid) + "\",\n" +
                "  \"age\": 18,\n" +
                "  \"tags\": [{\n" +
                "    \"id\": " + (++uuid) + ",\n" +
                "    \"text\": \"JavaScript\"\n" +
                "  }, {\n" +
                "    \"id\": " + (++uuid) + ",\n" +
                "    \"text\": \"Java\"\n" +
                "  }]\n" +
                "}";
        // System.out.println(json);
        // System.out.println("----------------");
        return json;
    }
    private static String getArrayJsonStr(int count) {
        StringBuilder sb = new StringBuilder();
        sb.append("[");
        boolean isFirstElement = true;
        for (int i = 0; i < count; i++) {
            if (isFirstElement) {
                isFirstElement = false;
            } else {
                sb.append(",");
            }
            sb.append(getJsonStr());
        }
        sb.append("]");
        return sb.toString();
    }

    /**
     * ========== usingJsonParser ==========
     */
    private static JsonParser jsonParser = new JsonParser();

    private static void deserializeObjectUsingJsonParser() {
        String json = getJsonStr();

        // parse
        // JsonParser jsonParser = new JsonParser();
        JsonElement userJsonElement = jsonParser.parse(json);
        JsonObject userJsonObject = userJsonElement.getAsJsonObject();

        // getValue and log
        System.out.println("name: " + userJsonObject.get("name").getAsString());
        System.out.println("age: " + userJsonObject.get("age").getAsInt());
        JsonArray userTagsJsonArray = userJsonObject.get("tags").getAsJsonArray();
        for (int i = 0; i < userTagsJsonArray.size(); i++) {
            JsonObject userTagJsonObject = userTagsJsonArray.get(i).getAsJsonObject();
            System.out.println(
                    "tag " + userTagJsonObject.get("id").getAsInt()
                    + ": " + userTagJsonObject.get("text").getAsString()
            );
        }
        System.out.println(userJsonObject.toString());
    }

    private static void deserializeArrayUsingJsonParser() {
        String json = getArrayJsonStr(3);

        JsonArray userJsonArray = jsonParser.parse(json).getAsJsonArray();

        // getValue and log
        for (int i = 0; i < userJsonArray.size(); i++) {
            JsonObject userJsonObject = userJsonArray.get(i).getAsJsonObject();
            System.out.println("user " + (i+1) + ": " + userJsonObject.get("name").getAsString());
        }

    }

    /**
     * ========== usingClass ==========
     */
    private class Tag {
        private int id;
        private String text;

        public int getId() {
            return id;
        }
        public String getText() {
            return text;
        }
    }
    private class User {
        private String name;
        private int age;
        private List<Tag> tags;

        public String getName() {
            return name;
        }
        public int getAge() {
            return age;
        }
        public List<Tag> getTags() {
            return tags;
        }
    }
    private static Gson gson = new Gson();

    private static void deserializeObjectUsingClass() {
        String json = getJsonStr();

        // Gson gson = new Gson();
        // parse
        User user = gson.fromJson(json, User.class);

        // getValue and log
        System.out.println("name: " + user.getName());
        System.out.println("age: " + user.getAge());
        List<Tag> tags = user.getTags();
        for (int i = 0; i < tags.size(); i++) {
            Tag tag = tags.get(i);
            System.out.println("tag " + tag.getId() + ": " + tag.getText());
        }
        System.out.println(gson.toJson(user));
    }


    private static Type type = null;
    static {
        TypeToken typeToken = new TypeToken<List<User>>() {};
        type = typeToken.getType();
    }
    private static void deserializeArrayUsingClass() {
        String json = getArrayJsonStr(3);

        // parse
        List<User> users = gson.fromJson(json, type);

        // getValue and log
        for (int i = 0; i < users.size(); i++) {
            System.out.println("user " + (i+1) + ": " + users.get(i).getName());
        }

    }



    /**
     * ========== usingGsonBuilder ==========
     */
    private class BaseUser {
        // import com.google.gson.annotations.Expose;
        @Expose(serialize = false, deserialize = true)
        private int id;

        @Expose
        private String name;

        @Expose
        @SerializedName("registrationTime")
        private Date registration;

        public int getId() {
            return id;
        }
        public Date getRegistration() {
            return registration;
        }
    }
    /* ---------- CustomAUser ---------- */
    private static class CustomUserData {
        private JsonElement ctx;
        public CustomUserData(JsonElement ctx) {
            this.ctx = ctx;
        }
        public String toString() {
            return this.ctx.toString();
        }
    }
    private class CustomAUser extends BaseUser {
        @Expose
        private CustomUserData data;

        public CustomUserData getData() {
            return data;
        }
    }
    // 自定义反序列化方法
    private static class CustomUserDataDeserializeAdapter implements JsonDeserializer<CustomUserData> {
        @Override
        public CustomUserData deserialize(JsonElement json, Type typeOfT, JsonDeserializationContext context)
                throws JsonParseException {
            return new CustomUserData(json);
        }
    }
    // 自定义序列化方法
    public static class CustomUserDataSerializeAdapter implements JsonSerializer<CustomUserData> {
        @Override
        public JsonElement serialize(CustomUserData src, Type typeOfSrc, JsonSerializationContext context) {
            return src.ctx;
        }
    }
    /* ---------- CustomBUser ---------- */
    private class CustomBUser extends BaseUser {
        @Expose
        private JsonElement data;

        public JsonElement getData() {
            return data;
        }
    }

    private static void deserializeObjectUsingGsonBuilder() {
        String json = "\n" +
                "{\n" +
                "  \"id\": 3,\n" +
                "  \"name\": \"Hans Chan\",\n" +
                "  \"registrationTime\": \"1999-09-19 18:10:22\",\n" +
                "  \"data\": {\n" +
                "    \"some\": \"complex data\",\n" +
                "    \"we\": {\n" +
                "      \"do-NOT\": [\"care\", \"about\"]\n" +
                "    }\n" +
                "  }\n" +
                "}";
        Gson deserializationGson = new GsonBuilder()
                .excludeFieldsWithoutExposeAnnotation()
                .setDateFormat("yyyy-MM-dd HH:mm:ss")
                .registerTypeAdapter(CustomUserData.class, new CustomUserDataDeserializeAdapter())
                .create();
        Gson serializationGson = new GsonBuilder()
                .excludeFieldsWithoutExposeAnnotation()
                .setDateFormat("yyyy/MM/dd HH:mm:ss")
                .registerTypeAdapter(CustomUserData.class, new CustomUserDataSerializeAdapter())
                .setPrettyPrinting()
                .create();

        System.out.println("---------- CustomAUser ----------");
        CustomAUser cau = deserializationGson.fromJson(json, CustomAUser.class);
        System.out.println("id: " + cau.getId());
        // System.out.println(cau.getRegistration());
        // System.out.println(cau.getData()); // .toString()
        System.out.println(serializationGson.toJson(cau));


        System.out.println("---------- CustomBUser ----------");
        CustomBUser cbu = deserializationGson.fromJson(json, CustomBUser.class);
        System.out.println("id: " + cbu.getId());
        // System.out.println(cbu.getRegistration());
        // System.out.println(cbu.getData()); // .toString()
        System.out.println(serializationGson.toJson(cbu));
    }


    /**
     * Main
     */
    public static void main(String args[]) {
        System.out.println("============================");
        System.out.println("========== object ==========");
        System.out.println("============================");

        System.out.println("");
        System.out.println("========== usingJsonParser ==========");
        deserializeObjectUsingJsonParser();

        System.out.println("");
        System.out.println("========== usingClass ==========");
        deserializeObjectUsingClass();

        System.out.println("");
        System.out.println("===========================");
        System.out.println("========== array ==========");
        System.out.println("===========================");

        System.out.println("");
        System.out.println("========== usingJsonParser ==========");
        deserializeArrayUsingJsonParser();

        System.out.println("");
        System.out.println("========== usingClass ==========");
        deserializeArrayUsingClass();

        System.out.println("");
        System.out.println("========== usingGsonBuilder ==========");
        deserializeObjectUsingGsonBuilder();
    }
}
