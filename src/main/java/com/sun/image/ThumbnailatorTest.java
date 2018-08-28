package com.sun.image;

import net.coobird.thumbnailator.Thumbnails;
import net.coobird.thumbnailator.geometry.Positions;

import java.io.IOException;

public class ThumbnailatorTest {
    public static void main(String[] args) throws IOException {

        Thumbnails.of("tttt.jpg")
                //.sourceRegion(Positions.CENTER,480,300)
                .scale(0.5)
                .outputQuality(0.5f)
                .toFile("2222222.jpg");

    }
}
