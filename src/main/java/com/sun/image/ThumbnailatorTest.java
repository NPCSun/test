package com.sun.image;

import net.coobird.thumbnailator.Thumbnails;
import net.coobird.thumbnailator.geometry.Positions;

import java.io.IOException;

public class ThumbnailatorTest {
    public static void main(String[] args) throws IOException {

        Thumbnails.of("1.jpg")
                .sourceRegion(Positions.CENTER,960,600)
                .size(480,300)
                .keepAspectRatio(false)
                .toFile("tttt.jpg");

    }
}
