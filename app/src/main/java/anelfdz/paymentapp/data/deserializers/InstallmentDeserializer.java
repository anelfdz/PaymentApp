package anelfdz.paymentapp.data.deserializers;

import com.google.gson.Gson;
import com.google.gson.JsonArray;
import com.google.gson.JsonDeserializationContext;
import com.google.gson.JsonDeserializer;
import com.google.gson.JsonElement;
import com.google.gson.JsonObject;
import com.google.gson.JsonParseException;
import com.google.gson.reflect.TypeToken;

import java.lang.reflect.Type;
import java.util.List;

import anelfdz.paymentapp.data.vo.Installment;

public class InstallmentDeserializer implements JsonDeserializer<List<Installment>> {

    @Override
    public List<Installment> deserialize(JsonElement json, Type typeOfT,
                                         JsonDeserializationContext context) throws JsonParseException {
        if (json.isJsonArray()) {
            JsonArray jsonArray = json.getAsJsonArray();

            if (jsonArray.size() > 0) {
                JsonElement jsonElement = jsonArray.get(0);

                if (jsonElement.isJsonObject()) {
                    JsonObject jsonObject = jsonElement.getAsJsonObject();

                    if (jsonObject.has("payer_costs")) {
                        JsonArray payerCosts =
                                jsonObject.getAsJsonArray("payer_costs");
                        Gson gson = new Gson();

                        Type typeOfList = new TypeToken<List<Installment>>(){}.getType();
                        return gson.fromJson(payerCosts, typeOfList);
                    }
                }
            }
        }

        return null;
    }
}
