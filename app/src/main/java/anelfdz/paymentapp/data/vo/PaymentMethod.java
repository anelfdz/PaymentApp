package anelfdz.paymentapp.data.vo;

import android.os.Parcel;
import android.os.Parcelable;

import com.google.gson.annotations.SerializedName;

import androidx.annotation.NonNull;
import androidx.room.Entity;
import androidx.room.Ignore;

@Entity(primaryKeys = {"id"})
public class PaymentMethod implements Parcelable {

    public static String CREDIT_CARD_TYPE = "credit_card";

    @NonNull
    @SerializedName("id")
    private String id;

    @NonNull
    @SerializedName("name")
    private String name;

    @NonNull
    @SerializedName("secure_thumbnail")
    private String thumbnailUrl;

    @NonNull
    @SerializedName("status")
    private String status;

    @NonNull
    @SerializedName("payment_type_id")
    private String paymentType;

    public PaymentMethod() {
    }

    @Ignore
    public PaymentMethod(@NonNull String id, @NonNull String name, @NonNull String thumbnailUrl,
                         @NonNull String status, @NonNull String paymentType) {
        this.id = id;
        this.name = name;
        this.thumbnailUrl = thumbnailUrl;
        this.status = status;
        this.paymentType = paymentType;
    }

    @SuppressWarnings("ConstantConditions")
    protected PaymentMethod(Parcel in) {
        id = in.readString();
        name = in.readString();
        thumbnailUrl = in.readString();
        status = in.readString();
        paymentType = in.readString();
    }

    public static final Creator<PaymentMethod> CREATOR = new Creator<PaymentMethod>() {
        @Override
        public PaymentMethod createFromParcel(Parcel in) {
            return new PaymentMethod(in);
        }

        @Override
        public PaymentMethod[] newArray(int size) {
            return new PaymentMethod[size];
        }
    };

    @NonNull
    public String getId() {
        return id;
    }

    public void setId(@NonNull String id) {
        this.id = id;
    }

    @NonNull
    public String getName() {
        return name;
    }

    public void setName(@NonNull String name) {
        this.name = name;
    }

    @NonNull
    public String getThumbnailUrl() {
        return thumbnailUrl;
    }

    public void setThumbnailUrl(@NonNull String thumbnailUrl) {
        this.thumbnailUrl = thumbnailUrl;
    }

    @NonNull
    public String getStatus() {
        return status;
    }

    public void setStatus(@NonNull String status) {
        this.status = status;
    }

    @NonNull
    public String getPaymentType() {
        return paymentType;
    }

    public void setPaymentType(@NonNull String paymentType) {
        this.paymentType = paymentType;
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;

        PaymentMethod that = (PaymentMethod) o;

        if (!id.equals(that.id)) return false;
        if (!name.equals(that.name)) return false;
        if (!thumbnailUrl.equals(that.thumbnailUrl)) return false;
        if (!status.equals(that.status)) return false;
        return paymentType.equals(that.paymentType);
    }

    @Override
    public int hashCode() {
        int result = id.hashCode();
        result = 31 * result + name.hashCode();
        result = 31 * result + thumbnailUrl.hashCode();
        result = 31 * result + status.hashCode();
        result = 31 * result + paymentType.hashCode();
        return result;
    }

    @NonNull
    @Override
    public String toString() {
        return "PaymentMethod{" +
                "id='" + id + '\'' +
                ", name='" + name + '\'' +
                ", thumbnailUrl='" + thumbnailUrl + '\'' +
                ", status='" + status + '\'' +
                ", paymentType='" + paymentType + '\'' +
                '}';
    }

    @Override
    public int describeContents() {
        return 0;
    }

    @Override
    public void writeToParcel(Parcel dest, int flags) {
        dest.writeString(id);
        dest.writeString(name);
        dest.writeString(thumbnailUrl);
        dest.writeString(status);
        dest.writeString(paymentType);
    }
}
