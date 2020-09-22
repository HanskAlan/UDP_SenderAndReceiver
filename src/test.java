import model.TransmissionData;

import java.util.Arrays;

public class test {
    public static void main(String[] args) {
        TransmissionData transmissionData = new TransmissionData(
                1,2,
                3,4,
                "10.0.0.1","10.0.0.1",
                7,8
        );
        System.out.println(transmissionData);
        String str = transmissionData.toString();
        System.out.println(Arrays.toString(str.split(", ")));
        System.out.println(TransmissionData.getTransmissionData(transmissionData.toString()));
    }
}
