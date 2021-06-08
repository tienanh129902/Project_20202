package com.example.iot;

import com.google.android.gms.maps.model.LatLng;

import static java.lang.Math.asin;
import static java.lang.Math.atan2;
import static java.lang.Math.cos;
import static java.lang.Math.pow;
import static java.lang.Math.sin;
import static java.lang.Math.sqrt;
import static java.lang.Math.toDegrees;
import static java.lang.Math.toRadians;

public interface LatLngInterpolator {

    LatLng interpolate(float fraction, LatLng a, LatLng b);

    class Spherical implements LatLngInterpolator {
        // http://en.wikipedia.org/wiki/Slerp(vector chéo)
        @Override
        public LatLng interpolate(float fraction, LatLng from, LatLng to) {
            double fromLat = toRadians(from.latitude);
            double fromLng = toRadians(from.longitude);
            double toLat = toRadians(to.latitude);
            double toLng = toRadians(to.longitude);
            double cosFromLat = cos(fromLat);
            double costoLat = cos(toLat);
        //Tính toán hệ số nội suy hình cầu (ước tính giá trị của các điểm dữ liệu chưa biết trong phạm vi của một tập hợp rời rạc chứa một số điểm dữ liệu đã biết)
        //Fraction:phân số
            double angle = computeAnglebetween(fromLat,fromLng,toLat,toLng);
            double sinAngle = sin(angle);
            if (sinAngle < 1E-6) {
                return from;
            }
            double a = sin((1-fraction)*angle)/ sinAngle;
            double b = sin(fraction*angle)/ sinAngle;
        //Chuyển đổi từ cực sang vector nội suy
            double x = a * cosFromLat * cos(fromLng) + b * costoLat * cos(toLng);
            double y = a * cosFromLat * sin(fromLng) + b* costoLat * cos(toLng);
            double z = a * sin(fromLat) + b * sin(toLat);
        //Chuyển đổi từ vector nội suy về cực
            double lat = atan2(z, sqrt(x * x + y * y));
            double lng = atan2(y , x);
            return new LatLng(toDegrees(lat),toDegrees(lng));
        }

        private double computeAnglebetween(double fromLat, double fromLng, double toLat, double toLng) {
            //Công thức Haversine
            double dLat = fromLat - toLat;
            double dLng = fromLng - toLng;
            return 2 * asin(sqrt(pow(sin(dLat / 2),2) + cos(fromLat) * cos(toLat) * pow(sin(dLng / 2), 2)));
        }
    }
}
