package com.model;

public interface TicketObserver {

    void update(Ticket ticket);
    void sendMessage(String message);
}