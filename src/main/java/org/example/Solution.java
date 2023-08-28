package org.example;

import com.fasterxml.jackson.databind.ObjectMapper;

import java.io.File;
import java.io.IOException;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Collections;
import java.util.Date;
import java.util.List;


public class Solution {
    public static void main(String[] args) throws IOException, ParseException {
        String file = args.length != 0 ? args[0] : "tickets.json";
        TicketsList ticketsList = jsonReader(file);
        String res = calculateFlightTime(ticketsList, "Владивосток", "Тель-Авив");
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
     * Расчет среднего времени полета между указанными городами
     */
    public static String calculateFlightTime(TicketsList ticketsList, String originName, String destinationName) throws ParseException {
        StringBuilder result = new StringBuilder();
        List<Ticket> list = ticketsList.getTickets();
        long sum = 0;
        SimpleDateFormat dateFormat = new SimpleDateFormat("dd.MM.yy'T'HH:mm");
        long travelTime = 0;
        int count = 0;

        for (Ticket ticket : list) {
            if (ticket.getOrigin_name().equals(originName) && ticket.getDestination_name().equals(destinationName)) {
                Date start = dateFormat.parse(ticket.getDeparture_date() + "T" + ticket.getDeparture_time());
                Date finish = dateFormat.parse(ticket.getArrival_date() + "T" + ticket.getArrival_time());
                travelTime = finish.getTime() - start.getTime();
                sum += travelTime;
                count++;
            }
        }

        if (count == 0) {
            return "Нет данных о полетах между городами " + originName + " и " + destinationName + ".";
        }

        sum /= count;
        //Получение целого количества часов из миллисекунд
        int hours = (int) sum / 1000 / 3600;
        //Получение целого количества минут из миллисекунд
        int minutes = (int) sum / 1000 / 60 % 60;
        result.append(String.format("Среднее время полета между городами %s и %s составляет:%n%d ч. и %d мин.%n",
                originName, destinationName, hours, minutes));

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
