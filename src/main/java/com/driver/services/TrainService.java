package com.driver.services;

import com.driver.EntryDto.AddTrainEntryDto;
import com.driver.EntryDto.SeatAvailabilityEntryDto;
import com.driver.model.Passenger;
import com.driver.model.Station;
import com.driver.model.Ticket;
import com.driver.model.Train;
import com.driver.repository.TrainRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.time.LocalTime;
import java.util.ArrayList;
import java.util.List;

@Service
public class TrainService {

    @Autowired
    TrainRepository trainRepository;

    public Integer addTrain(AddTrainEntryDto trainEntryDto){
        //Add the train to the trainRepository
        //and route String logic to be taken from the Problem statement.
        //Save the train and return the trainId that is generated from the database.
        //Avoid using the lombok library
        Train train=new Train();
        train.setDepartureTime(trainEntryDto.getDepartureTime());
        train.setNoOfSeats(trainEntryDto.getNoOfSeats());

        String route="";
        for(Station s:trainEntryDto.getStationRoute()) route=route+","+s.toString();
        route=route.substring(1);

        train.setRoute(route);
        Train saved_train=trainRepository.save(train);

        return saved_train.getTrainId();
    }

    public Integer calculateAvailableSeats(SeatAvailabilityEntryDto seatAvailabilityEntryDto){
        //Calculate the total seats available
        //Suppose the route is A B C D
        //And there are 2 seats available in total in the train and 2 tickets are booked from A to C and B to D.
        //The seat is available only between A to C and A to B.
        // If a seat is empty between 2 station it will be counted to our final ans
        //even if that seat is booked post the destStation or before the boardingStation
        //In short : a train has totalNo of seats and there are tickets from and to different locations
        //We need to find out the available seats between the given 2 stations.
        Station start=seatAvailabilityEntryDto.getFromStation();
        Station end=seatAvailabilityEntryDto.getToStation();
        Train train=trainRepository.findById(seatAvailabilityEntryDto.getTrainId()).get();

        int count=0;

       return count;
    }

    public Integer calculatePeopleBoardingAtAStation(Integer trainId,Station station) throws Exception{

        //We need to find out the number of people who will be boarding a train from a particular station
        //if the trainId is not passing through that station
        //throw new Exception("Train is not passing from this station");
        //in a happy case we need to find out the number of such people.
        boolean found=false;
        Train train=trainRepository.findById(trainId).get();
        String[] stations=train.getRoute().split(",");
        for(String stat:stations){
            if(stat.equalsIgnoreCase(station.toString())) found=true;
        }
        int count=0;
        if(found){
            for(Ticket ticket:train.getBookedTickets()){
                if(ticket.getFromStation().equals(station)) count+=ticket.getPassengersList().size();
            }
            return count;
        }
        else throw new Exception("Train is not passing from this station");
    }

    public Integer calculateOldestPersonTravelling(Integer trainId){

        //Throughout the journey of the train between any 2 stations
        //We need to find out the age of the oldest person that is travelling the train
        //If there are no people travelling in that train you can return 0
        int age=0;
        Train train=trainRepository.findById(trainId).get();
        for(Ticket ticket:train.getBookedTickets()){
            for(Passenger passenger:ticket.getPassengersList())
                age=Math.max(age,passenger.getAge());
        }
        return age;
    }

    public List<Integer> trainsBetweenAGivenTime(Station station, LocalTime startTime, LocalTime endTime){
        //When you are at a particular station you need to find out the number of trains that will pass through a given station
        //between a particular time frame both start time and end time included.
        //You can assume that the date change doesn't need to be done ie the travel will certainly happen with the same date
        //(More details in problem statement)
        //You can also assume the seconds and milliseconds value will be 0 in a LocalTime format.
        List<Train> trains=trainRepository.findAll();
        List<Integer> trainIds=new ArrayList<>();
        for(Train train:trains){
            String[] stations=train.getRoute().split(",");
            LocalTime timeAtStation=train.getDepartureTime();
            for(int i=1;i<stations.length;i++){
                timeAtStation=timeAtStation.plusHours(1);
                if(stations[i].equalsIgnoreCase(station.toString()) && timeAtStation.compareTo(startTime)>=0 && timeAtStation.compareTo(endTime)<=0)
                    trainIds.add(train.getTrainId());
            }
        }

        return trainIds;
    }

}
