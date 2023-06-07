package com.example.kanjiapplication.imagetransformation;

public class SearchExtremePoints {

    private final int MIN_COLOR = -1;
    private int height;
    private int width;
    private int[] pixels;

    public SearchExtremePoints(int height, int width, int[] pixels) {
        this.height = height;
        this.width = width;
        this.pixels = pixels;
    }

    public int searchExtremePoint(String direction) {
        int extremePoint = -1;
        boolean key = false;
        switch (direction) {
            case "left":
                for (int j = 0; j < width; j++) {
                    for (int i = 0; i < height; i++)
                        if (pixels[i * width + j] != MIN_COLOR) {
                            extremePoint = j;
                            key = true;
                            break;
                        }
                    if (key)
                        break;
                }
                break;
            case "right":
                for (int j = width - 1; j >= 0; j--) {
                    for (int i = 0; i < height; i++)
                        if (pixels[i * width + j] != MIN_COLOR) {
                            extremePoint = j;
                            key = true;
                            break;
                        }
                    if (key)
                        break;
                }
                break;
            case "top":
                for (int i = 0; i < height; i++) {
                    for (int j = 0; j < width; j++)
                        if (pixels[i * width + j] != MIN_COLOR) {
                            extremePoint = i;
                            key = true;
                            break;
                        }
                    if (key)
                        break;
                }
                break;
            case "down":
                for (int i = height - 1; i >= 0; i--) {
                    for (int j = 0; j < width; j++)
                        if (pixels[i * width + j] != MIN_COLOR) {
                            extremePoint = i;
                            key = true;
                            break;
                        }
                    if (key)
                        break;
                }
                break;
            default:
                break;
        }
        return extremePoint;
    }
}
