package com.pence.service;

import org.springframework.stereotype.Service;

import javax.imageio.ImageIO;
import java.awt.*;
import java.awt.image.BufferedImage;
import java.io.IOException;
import java.io.InputStream;

@Service
public class ImageHashService {

    private static final int HASH_SIZE = 8;
    private static final int RESIZE    = 32;

    public String calculateHash(InputStream imageStream) throws IOException {
        BufferedImage image = ImageIO.read(imageStream);
        if (image == null) throw new IOException("Şəkil oxuna bilmədi!");
        return calculateHash(image);
    }

    public String calculateHash(BufferedImage image) {
        BufferedImage small = resize(image, RESIZE, RESIZE);
        double[][] gray     = toGrayscale(small);
        double[][] dct      = applyDCT(gray);
        double[]   values   = extractTopLeft(dct);
        double     mean     = calculateMean(values);

        StringBuilder hash = new StringBuilder();
        for (int i = 1; i < values.length; i++) {
            hash.append(values[i] > mean ? "1" : "0");
        }
        return hash.toString();
    }

    public long hammingDistance(String h1, String h2) {
        if (h1 == null || h2 == null || h1.length() != h2.length()) return Long.MAX_VALUE;
        long dist = 0;
        for (int i = 0; i < h1.length(); i++) {
            if (h1.charAt(i) != h2.charAt(i)) dist++;
        }
        return dist;
    }

    public double toSimilarityPercent(long distance) {
        int max = HASH_SIZE * HASH_SIZE - 1;
        return Math.max(0, (1.0 - (double) distance / max) * 100);
    }

    // ------------------------------------------------

    private BufferedImage resize(BufferedImage img, int w, int h) {
        BufferedImage r = new BufferedImage(w, h, BufferedImage.TYPE_INT_RGB);
        Graphics2D g = r.createGraphics();
        g.drawImage(img, 0, 0, w, h, null);
        g.dispose();
        return r;
    }

    private double[][] toGrayscale(BufferedImage img) {
        int w = img.getWidth(), h = img.getHeight();
        double[][] gray = new double[h][w];
        for (int y = 0; y < h; y++) {
            for (int x = 0; x < w; x++) {
                Color c = new Color(img.getRGB(x, y));
                gray[y][x] = 0.299 * c.getRed() + 0.587 * c.getGreen() + 0.114 * c.getBlue();
            }
        }
        return gray;
    }

    private double[][] applyDCT(double[][] input) {
        int n = input.length;
        double[][] dct = new double[n][n];
        for (int u = 0; u < n; u++) {
            for (int v = 0; v < n; v++) {
                double sum = 0;
                for (int x = 0; x < n; x++) {
                    for (int y = 0; y < n; y++) {
                        sum += input[x][y]
                             * Math.cos((2*x+1) * u * Math.PI / (2*n))
                             * Math.cos((2*y+1) * v * Math.PI / (2*n));
                    }
                }
                double cu = (u == 0) ? 1.0 / Math.sqrt(2) : 1.0;
                double cv = (v == 0) ? 1.0 / Math.sqrt(2) : 1.0;
                dct[u][v] = (2.0 / n) * cu * cv * sum;
            }
        }
        return dct;
    }

    private double[] extractTopLeft(double[][] dct) {
        double[] res = new double[HASH_SIZE * HASH_SIZE];
        int idx = 0;
        for (int i = 0; i < HASH_SIZE; i++)
            for (int j = 0; j < HASH_SIZE; j++)
                res[idx++] = dct[i][j];
        return res;
    }

    private double calculateMean(double[] values) {
        double sum = 0;
        for (int i = 1; i < values.length; i++) sum += values[i];
        return sum / (values.length - 1);
    }
}
