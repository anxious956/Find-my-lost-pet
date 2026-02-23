package com.pence.dto;

import java.util.List;

public class MatchResultDTO<T> {

    private boolean matchFound;
    private double similarityPercent;
    private List<MatchEntry<T>> matches;
    private int totalSearched;
    private String message;

    // ------------------------------------------------
    // CONSTRUCTOR-LAR
    // ------------------------------------------------

    public MatchResultDTO() {}

    public MatchResultDTO(boolean matchFound, double similarityPercent,
                          List<MatchEntry<T>> matches,
                          int totalSearched, String message) {
        this.matchFound        = matchFound;
        this.similarityPercent = similarityPercent;
        this.matches           = matches;
        this.totalSearched     = totalSearched;
        this.message           = message;
    }

    // ------------------------------------------------
    // GETTER / SETTER
    // ------------------------------------------------

    public boolean isMatchFound()              { return matchFound; }
    public double getSimilarityPercent()       { return similarityPercent; }
    public List<MatchEntry<T>> getMatches()    { return matches; }
    public int getTotalSearched()              { return totalSearched; }
    public String getMessage()                 { return message; }

    public void setMatchFound(boolean v)              { this.matchFound = v; }
    public void setSimilarityPercent(double v)        { this.similarityPercent = v; }
    public void setMatches(List<MatchEntry<T>> v)     { this.matches = v; }
    public void setTotalSearched(int v)               { this.totalSearched = v; }
    public void setMessage(String v)                  { this.message = v; }

    // ------------------------------------------------
    // İç sinif — hər bir uyğunluq nəticəsi
    // ------------------------------------------------

    public static class MatchEntry<T> {

        private T pet;
        private double similarity;
        private long hashDistance;

        public MatchEntry() {}

        public MatchEntry(T pet, double similarity, long hashDistance) {
            this.pet          = pet;
            this.similarity   = similarity;
            this.hashDistance = hashDistance;
        }

        public T getPet()                { return pet; }
        public double getSimilarity()    { return similarity; }
        public long getHashDistance()    { return hashDistance; }

        public void setPet(T pet)                      { this.pet = pet; }
        public void setSimilarity(double similarity)   { this.similarity = similarity; }
        public void setHashDistance(long hashDistance) { this.hashDistance = hashDistance; }
    }

    // ------------------------------------------------
    // Static factory metodlar
    // ------------------------------------------------

    public static <T> MatchResultDTO<T> noMatch(int totalSearched) {
        return new MatchResultDTO<>(false, 0, List.of(), totalSearched, "Uyğun heyvan tapılmadı.");
    }

    public static <T> MatchResultDTO<T> withMatches(List<MatchEntry<T>> matches, int totalSearched) {
        double best = 0;
        for (MatchEntry<T> e : matches) {
            if (e.getSimilarity() > best) best = e.getSimilarity();
        }
        return new MatchResultDTO<>(true, best, matches, totalSearched,
                matches.size() + " uyğun heyvan tapıldı!");
    }
}
