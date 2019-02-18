package anelfdz.paymentapp.data.vo;

import android.os.Parcel;
import android.os.Parcelable;

import com.google.gson.annotations.SerializedName;

import androidx.annotation.NonNull;
import androidx.room.Entity;
import androidx.room.Ignore;

@Entity(primaryKeys = {"id"})
public class Bank implements Parcelable {

    @NonNull
    @SerializedName("id")
    private String id;

    @NonNull
    @SerializedName("name")
    private String name;

    @NonNull
    @SerializedName("secure_thumbnail")
    private String thumbnailUrl;

    private String paymentMethodId;

    public Bank() {
    }

    @Ignore
    public Bank(@NonNull String id, @NonNull String name, @NonNull String thumbnailUrl,
                String paymentMethodId) {
        this.id = id;
        this.name = name;
        this.thumbnailUrl = thumbnailUrl;
        this.paymentMethodId = paymentMethodId;
    }

    @SuppressWarnings("ConstantConditions")
    protected Bank(Parcel in) {
        id = in.readString();
        name = in.readString();
        thumbnailUrl = in.readString();
        paymentMethodId = in.readString();
    }

    public static final Creator<Bank> CREATOR = new Creator<Bank>() {
        @Override
        public Bank createFromParcel(Parcel in) {
            return new Bank(in);
        }

        @Override
        public Bank[] newArray(int size) {
            return new Bank[size];
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

    public String getPaymentMethodId() {
        return paymentMethodId;
    }

    public void setPaymentMethodId(String paymentMethodId) {
        this.paymentMethodId = paymentMethodId;
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;

        Bank bank = (Bank) o;

        if (!id.equals(bank.id)) return false;
        if (!name.equals(bank.name)) return false;
        if (!thumbnailUrl.equals(bank.thumbnailUrl)) return false;
        return paymentMethodId != null ? paymentMethodId.equals(bank.paymentMethodId) : bank.paymentMethodId == null;
    }

    @Override
    public int hashCode() {
        int result = id.hashCode();
        result = 31 * result + name.hashCode();
        result = 31 * result + thumbnailUrl.hashCode();
        result = 31 * result + (paymentMethodId != null ? paymentMethodId.hashCode() : 0);
        return result;
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
        dest.writeString(paymentMethodId);
    }
}
