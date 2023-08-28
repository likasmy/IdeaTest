package org.example;

import com.fasterxml.jackson.databind.ObjectMapper;

import java.io.File;
import java.io.IOException;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.*;


public class Solution {
    public static void main(String[] args) throws IOException, ParseException {
        String file = args.length != 0 ? args[0] : "tickets.json";
        TicketsList ticketsList = jsonReader(file);
        String res = fight(ticketsList, "Владивосток", "Тель-Авив");
        //Вывод ответа в консоль
        System.out.println(res);
        String priceDiff = calculatePriceDifference(ticketsList, "Владивосток", "Тель-Авив");
        System.out.println(priceDiff);
    }

    /**
     * Json-mapper
     */
    private static TicketsList jsonReader(String file) throws IOException {
        ObjectMapper mapper = new ObjectMapper();
        return mapper.readValue(new File(file), TicketsList.class);
    }

    /**
     * Расчет мин. времени полета между указанными городами
     */
    public static String fight(TicketsList ticketsList, String originName, String destinationName) {
        StringBuilder result = new StringBuilder();
        List<Ticket> list = ticketsList.getTickets();
        Map<String, Long> minTravelTimes = new HashMap<>();
        SimpleDateFormat dateFormat = new SimpleDateFormat("dd.MM.yy'T'HH:mm");

        for (Ticket ticket : list) {
            if (ticket.getOrigin_name().equals(originName) && ticket.getDestination_name().equals(destinationName)) {
                String carrier = ticket.getCarrier();
                long minTravelTime = minTravelTimes.getOrDefault(carrier, Long.MAX_VALUE);
                Date start;
                Date finish;
                try {
                    start = dateFormat.parse(ticket.getDeparture_date() + "T" + ticket.getDeparture_time());
                    finish = dateFormat.parse(ticket.getArrival_date() + "T" + ticket.getArrival_time());
                    long travelTime = finish.getTime() - start.getTime();
                    if (travelTime < minTravelTime) {
                        minTravelTimes.put(carrier, travelTime);
                    }
                } catch (Exception e) {
                    e.printStackTrace();
                }
            }
        }

        for (String carrier : minTravelTimes.keySet()) {
            long minTravelTime = minTravelTimes.get(carrier);
            //Получение целого количества часов из миллисекунд
            int hours = (int) minTravelTime / 1000 / 3600;
            // Получение целого количества минут из миллисекунд
            int minutes = (int) minTravelTime / 1000 / 60 % 60;
            result.append(String.format("Минимальное время полета между городами %s и %s для перевозчика %s составляет:%n%d ч. и %d мин.%n",
                    originName, destinationName, carrier, hours, minutes));
        }
        return result.toString();
    }

    /**
     * Расчет разницы между средней ценой и медианой билетов на полет между указанными городами
     */
    public static String calculatePriceDifference(TicketsList ticketsList, String originName, String destinationName) throws ParseException {
        StringBuilder result = new StringBuilder();
        List<Ticket> list = ticketsList.getTickets();
        List<Double> prices = new ArrayList<>();

        for (Ticket ticket : list) {
            if (ticket.getOrigin_name().equals(originName) && ticket.getDestination_name().equals(destinationName)) {
                prices.add(ticket.getPrice());
            }
        }

        if (prices.isEmpty())
 {
            return "Нет данных о полетах между городами " + originName + " и " + destinationName + ".";
        }

        double averagePrice = prices.stream().mapToDouble(Double::doubleValue).average().getAsDouble();
        double medianPrice = calculateMedian(prices);
        double priceDifference = averagePrice - medianPrice;

        result.append("Разница между средней ценой и медианой: ").append(priceDifference);

        return result.toString();
    }

    /**
     * Расчет медианы списка чисел
     */
    private static double calculateMedian(List<Double> list) {
        Collections.sort(list);
        int size = list.size();
        if (size % 2 == 0) {
            return (list.get(size / 2 - 1) + list.get(size / 2)) / 2;
        } else {
            return list.get(size / 2);
        }
    }
}
