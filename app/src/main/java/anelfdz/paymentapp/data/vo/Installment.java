package anelfdz.paymentapp.data.vo;

import android.os.Parcel;
import android.os.Parcelable;

import com.google.gson.annotations.SerializedName;

import androidx.annotation.NonNull;

public class Installment implements Parcelable {

    @SerializedName("installments")
    private int installments;

    @NonNull
    @SerializedName("recommended_message")
    private String recommendedMessage;

    @SerializedName("installment_amount")
    private double installmentAmount;

    @SerializedName("total_amount")
    private double totalAmount;

    public Installment(int installments, @NonNull String recommendedMessage, double installmentAmount, double totalAmount) {
        this.installments = installments;
        this.recommendedMessage = recommendedMessage;
        this.installmentAmount = installmentAmount;
        this.totalAmount = totalAmount;
    }

    @SuppressWarnings("ConstantConditions")
    protected Installment(Parcel in) {
        installments = in.readInt();
        recommendedMessage = in.readString();
        installmentAmount = in.readDouble();
        totalAmount = in.readDouble();
    }

    public static final Creator<Installment> CREATOR = new Creator<Installment>() {
        @Override
        public Installment createFromParcel(Parcel in) {
            return new Installment(in);
        }

        @Override
        public Installment[] newArray(int size) {
            return new Installment[size];
        }
    };

    public int getInstallments() {
        return installments;
    }

    public void setInstallments(int installments) {
        this.installments = installments;
    }

    @NonNull
    public String getRecommendedMessage() {
        return recommendedMessage;
    }

    public void setRecommendedMessage(@NonNull String recommendedMessage) {
        this.recommendedMessage = recommendedMessage;
    }

    public double getInstallmentAmount() {
        return installmentAmount;
    }

    public void setInstallmentAmount(double installmentAmount) {
        this.installmentAmount = installmentAmount;
    }

    public double getTotalAmount() {
        return totalAmount;
    }

    public void setTotalAmount(double totalAmount) {
        this.totalAmount = totalAmount;
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;

        Installment that = (Installment) o;

        if (installments != that.installments) return false;
        if (Double.compare(that.installmentAmount, installmentAmount) != 0) return false;
        if (Double.compare(that.totalAmount, totalAmount) != 0) return false;
        return recommendedMessage.equals(that.recommendedMessage);
    }

    @Override
    public int hashCode() {
        int result;
        long temp;
        result = installments;
        result = 31 * result + recommendedMessage.hashCode();
        temp = Double.doubleToLongBits(installmentAmount);
        result = 31 * result + (int) (temp ^ (temp >>> 32));
        temp = Double.doubleToLongBits(totalAmount);
        result = 31 * result + (int) (temp ^ (temp >>> 32));
        return result;
    }

    @Override
    public String toString() {
        return "Installment{" +
                "installments=" + installments +
                ", recommendedMessage='" + recommendedMessage + '\'' +
                ", installmentAmount=" + installmentAmount +
                ", totalAmount=" + totalAmount +
                '}';
    }

    @Override
    public int describeContents() {
        return 0;
    }

    @Override
    public void writeToParcel(Parcel dest, int flags) {
        dest.writeInt(installments);
        dest.writeString(recommendedMessage);
        dest.writeDouble(installmentAmount);
        dest.writeDouble(totalAmount);
    }
}
