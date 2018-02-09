package no.nav.fpsak.tidsserie;

import java.math.BigDecimal;
import java.math.BigInteger;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

/** Standard funksjoner for kombinere verdier fra to segmenter over et gitt interval. */
public class StandardCombinators {

    private static final Map<String, Number> ZEROS;

    static {
        ZEROS = new HashMap<>();
        ZEROS.put("Long", 0L);
        ZEROS.put("Integer", 0);
        ZEROS.put("Double", 0d);
        ZEROS.put("Float", 0f);
        ZEROS.put("Short", (short) 0);
        ZEROS.put("Byte", (byte) 0);
        ZEROS.put("BigDecimal", BigDecimal.ZERO);
        ZEROS.put("BigInteger", BigInteger.ZERO);
    }

    private StandardCombinators() {
        // private
    }

    /**
     * Returner liste alle verdier fra begge tidsserier angitt. Det anbefales først og fremst benyttet for tidsserier
     * med verdier av samme type, men det er ikke et krav.
     */
    @SuppressWarnings({ "rawtypes", "unchecked" })
    public static LocalDateSegment<List> allValues(LocalDateInterval dateInterval,
            LocalDateSegment<List<?>> lhs, LocalDateSegment<?> rhs) {
        List list = new ArrayList<>(lhs.getValue());
        list.add(rhs.getValue());
        return new LocalDateSegment<>(dateInterval, list);
    }

    /**
     * Basic combinator som alltid returnerer Boolean.TRUE for matching interval. Greit å bruke når verdi ikke betyr
     * noe, kun intervaller.
     */
    public static LocalDateSegment<Boolean> alwaysTrueForMatch(
            LocalDateInterval dateInterval,
            @SuppressWarnings("unused") LocalDateSegment<?> lhs, // NOSONAR
            @SuppressWarnings("unused") LocalDateSegment<?> rhs // NOSONAR
    ) {
        return new LocalDateSegment<>(dateInterval, Boolean.TRUE);
    }

    /** Returner begge verdier. */
    @SuppressWarnings("rawtypes")
    public static <T, V> LocalDateSegment<List> bothValues(LocalDateInterval dateInterval,
            LocalDateSegment<T> lhs, LocalDateSegment<V> rhs) {
        return new LocalDateSegment<>(dateInterval, Arrays.asList(lhs.getValue(), rhs.getValue()));
    }

    /** Basic combinator som alltid returnerer verdi fra første (Left-Hand Side) timeline hvis finnes, ellers andre. */
    public static <V> LocalDateSegment<V> coalesceLeftHandSide(LocalDateInterval dateInterval,
            LocalDateSegment<V> lhs, LocalDateSegment<V> rhs) {
        return lhs == null ? new LocalDateSegment<>(dateInterval, rhs.getValue()) : new LocalDateSegment<>(dateInterval, lhs.getValue());
    }

    /** Basic combinator som alltid returnerer verdi fra andre (Right-Hand Side) timeline hvis finnes, ellers første. */
    public static <V> LocalDateSegment<V> coalesceRightHandSide(LocalDateInterval dateInterval,
            LocalDateSegment<V> lhs, LocalDateSegment<V> rhs) {
        return rhs == null ? new LocalDateSegment<>(dateInterval, lhs.getValue()) : new LocalDateSegment<>(dateInterval, rhs.getValue());
    }

    /** Tar kun første (Left-Hand Side) verdi alltid, selv om null */
    public static <T, V> LocalDateSegment<T> leftOnly(LocalDateInterval dateInterval,
            LocalDateSegment<T> lhs,
            @SuppressWarnings("unused") LocalDateSegment<V> rhs // NOSONAR
    ) {
        return new LocalDateSegment<>(dateInterval, lhs.getValue());
    }

    /** Tar kun andre (Right-Hand Side) verdi alltid, selv om null */
    public static <T, V> LocalDateSegment<V> rightOnly(LocalDateInterval dateInterval,
            @SuppressWarnings("unused") LocalDateSegment<T> lhs, // NOSONAR
            LocalDateSegment<V> rhs) {
        return new LocalDateSegment<>(dateInterval, rhs.getValue());
    }

    /**
     * Basic combinator som sum av verdi fra begge segmenter
     */
    public static <L extends Number, R extends Number> LocalDateSegment<L> sum(LocalDateInterval dateInterval,
            LocalDateSegment<L> lhs, LocalDateSegment<R> rhs) {

        return new LocalDateSegment<>(dateInterval, sum(lhs.getValue(), rhs.getValue()));
    }
    
    @SuppressWarnings("unchecked")
    private static <L extends Number, R extends Number> L sum(L lhs, R rhs) {
        if (lhs == null && rhs == null) {
            return null; // kan ikke bestemme return type?
        } else if (lhs == null) {
            return (L) ZEROS.get(rhs.getClass().getSimpleName());
        } else if (rhs == null) {
            return (L) ZEROS.get(lhs.getClass().getSimpleName());
        }

        return sumNonNull(lhs, rhs);
    }

    @SuppressWarnings("unchecked")
    private static <L extends Number, R extends Number> L sumNonNull(L lhs, R rhs) {
        String type = lhs.getClass().getSimpleName();
        switch (type) {
            case "Long":
                return (L) Long.valueOf(lhs.longValue() + rhs.longValue());
            case "Integer":
                return (L) Integer.valueOf(lhs.intValue() + rhs.intValue());
            case "Double":
                return (L) Double.valueOf(lhs.doubleValue() + rhs.doubleValue());
            case "Float":
                return (L) Float.valueOf(lhs.floatValue() + rhs.floatValue());
            case "Short":
                return (L) Short.valueOf((short) (lhs.shortValue() + rhs.shortValue()));
            case "Byte":
                return (L) Byte.valueOf((byte) (lhs.byteValue() + rhs.byteValue()));
            case "BigDecimal":
                return (L) ((BigDecimal) lhs).add(new BigDecimal(rhs.toString()));
            case "BigInteger":
                return (L) ((BigInteger) lhs).add(new BigInteger(rhs.toString()));
            default:
                return (L) Double.valueOf(lhs.doubleValue() + rhs.doubleValue());
        }
    }
}