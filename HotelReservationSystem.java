import java.io.*;
import java.time.LocalDate;
import java.time.temporal.ChronoUnit;
import java.util.*;

public class HotelReservationSystem {

    static final String ROOMS_FILE = "rooms.txt";
    static final String BOOKINGS_FILE = "bookings.txt";

    enum RoomCategory {
        STANDARD, DELUXE, SUITE
    }

    enum PaymentStatus {
        PENDING, PAID, CANCELLED, REFUNDED
    }

    static class Room {
        private String roomId;
        private RoomCategory category;
        private double pricePerNight;
        private boolean available;

        public Room(String roomId, RoomCategory category, double pricePerNight, boolean available) {
            this.roomId = roomId;
            this.category = category;
            this.pricePerNight = pricePerNight;
            this.available = available;
        }

        public String getRoomId() {
            return roomId;
        }

        public RoomCategory getCategory() {
            return category;
        }

        public double getPricePerNight() {
            return pricePerNight;
        }

        public boolean isAvailable() {
            return available;
        }

        public void setAvailable(boolean available) {
            this.available = available;
        }

        @Override
        public String toString() {
            return roomId + " | " + category + " | Rs." + pricePerNight + " | " +
                    (available ? "Available" : "Booked");
        }

        public String toFileString() {
            return roomId + "," + category + "," + pricePerNight + "," + available;
        }
    }

    static class Reservation {
        private String bookingId;
        private String customerName;
        private String customerPhone;
        private String roomId;
        private RoomCategory category;
        private LocalDate checkInDate;
        private LocalDate checkOutDate;
        private long totalDays;
        private double totalAmount;
        private PaymentStatus paymentStatus;

        public Reservation(String bookingId, String customerName, String customerPhone, String roomId,
                           RoomCategory category, LocalDate checkInDate, LocalDate checkOutDate,
                           long totalDays, double totalAmount, PaymentStatus paymentStatus) {
            this.bookingId = bookingId;
            this.customerName = customerName;
            this.customerPhone = customerPhone;
            this.roomId = roomId;
            this.category = category;
            this.checkInDate = checkInDate;
            this.checkOutDate = checkOutDate;
            this.totalDays = totalDays;
            this.totalAmount = totalAmount;
            this.paymentStatus = paymentStatus;
        }

        public String getBookingId() {
            return bookingId;
        }

        public String getCustomerName() {
            return customerName;
        }

        public String getRoomId() {
            return roomId;
        }

        public double getTotalAmount() {
            return totalAmount;
        }

        public PaymentStatus getPaymentStatus() {
            return paymentStatus;
        }

        public void setPaymentStatus(PaymentStatus paymentStatus) {
            this.paymentStatus = paymentStatus;
        }

        @Override
        public String toString() {
            return "\nBooking ID     : " + bookingId +
                   "\nCustomer Name  : " + customerName +
                   "\nPhone          : " + customerPhone +
                   "\nRoom ID        : " + roomId +
                   "\nCategory       : " + category +
                   "\nCheck-In Date  : " + checkInDate +
                   "\nCheck-Out Date : " + checkOutDate +
                   "\nDays           : " + totalDays +
                   "\nTotal Amount   : Rs." + totalAmount +
                   "\nPayment Status : " + paymentStatus + "\n";
        }

        public String toFileString() {
            return bookingId + "," + customerName + "," + customerPhone + "," + roomId + "," + category + "," +
                    checkInDate + "," + checkOutDate + "," + totalDays + "," + totalAmount + "," + paymentStatus;
        }
    }

    static class HotelManager {
        private ArrayList<Room> rooms = new ArrayList<>();
        private ArrayList<Reservation> reservations = new ArrayList<>();

        public HotelManager() {
            loadRoomsFromFile();
            loadReservationsFromFile();
        }

        private void loadRoomsFromFile() {
            File file = new File(ROOMS_FILE);

            if (!file.exists()) {
                createDefaultRooms();
                saveRoomsToFile();
                return;
            }

            try (BufferedReader br = new BufferedReader(new FileReader(file))) {
                String line;
                while ((line = br.readLine()) != null) {
                    String[] data = line.split(",");
                    if (data.length == 4) {
                        String roomId = data[0];
                        RoomCategory category = RoomCategory.valueOf(data[1]);
                        double price = Double.parseDouble(data[2]);
                        boolean available = Boolean.parseBoolean(data[3]);
                        rooms.add(new Room(roomId, category, price, available));
                    }
                }
            } catch (Exception e) {
                System.out.println("Error loading rooms: " + e.getMessage());
            }
        }

        private void createDefaultRooms() {
            rooms.add(new Room("R101", RoomCategory.STANDARD, 2000, true));
            rooms.add(new Room("R102", RoomCategory.STANDARD, 2000, true));
            rooms.add(new Room("R201", RoomCategory.DELUXE, 3500, true));
            rooms.add(new Room("R202", RoomCategory.DELUXE, 3500, true));
            rooms.add(new Room("R301", RoomCategory.SUITE, 5000, true));
        }

        private void saveRoomsToFile() {
            try (PrintWriter pw = new PrintWriter(new FileWriter(ROOMS_FILE))) {
                for (Room room : rooms) {
                    pw.println(room.toFileString());
                }
            } catch (IOException e) {
                System.out.println("Error saving rooms: " + e.getMessage());
            }
        }

        private void loadReservationsFromFile() {
            File file = new File(BOOKINGS_FILE);

            if (!file.exists()) {
                return;
            }

            try (BufferedReader br = new BufferedReader(new FileReader(file))) {
                String line;
                while ((line = br.readLine()) != null) {
                    String[] data = line.split(",");
                    if (data.length == 10) {
                        Reservation reservation = new Reservation(
                                data[0],
                                data[1],
                                data[2],
                                data[3],
                                RoomCategory.valueOf(data[4]),
                                LocalDate.parse(data[5]),
                                LocalDate.parse(data[6]),
                                Long.parseLong(data[7]),
                                Double.parseDouble(data[8]),
                                PaymentStatus.valueOf(data[9])
                        );
                        reservations.add(reservation);
                    }
                }
            } catch (Exception e) {
                System.out.println("Error loading bookings: " + e.getMessage());
            }
        }

        private void saveReservationsToFile() {
            try (PrintWriter pw = new PrintWriter(new FileWriter(BOOKINGS_FILE))) {
                for (Reservation reservation : reservations) {
                    pw.println(reservation.toFileString());
                }
            } catch (IOException e) {
                System.out.println("Error saving bookings: " + e.getMessage());
            }
        }

        public void viewAllRooms() {
            System.out.println("\n===== ALL ROOMS =====");
            for (Room room : rooms) {
                System.out.println(room);
            }
        }

        public void searchRooms(RoomCategory category) {
            boolean found = false;
            System.out.println("\n===== AVAILABLE " + category + " ROOMS =====");
            for (Room room : rooms) {
                if (room.getCategory() == category && room.isAvailable()) {
                    System.out.println(room);
                    found = true;
                }
            }
            if (!found) {
                System.out.println("No available rooms in this category.");
            }
        }

        public void bookRoom(Scanner sc) {
            try {
                System.out.print("Enter customer name: ");
                String name = sc.nextLine();

                System.out.print("Enter customer phone: ");
                String phone = sc.nextLine();

                RoomCategory category = readCategory(sc);

                System.out.print("Enter check-in date (yyyy-mm-dd): ");
                LocalDate checkIn = LocalDate.parse(sc.nextLine());

                System.out.print("Enter check-out date (yyyy-mm-dd): ");
                LocalDate checkOut = LocalDate.parse(sc.nextLine());

                long days = ChronoUnit.DAYS.between(checkIn, checkOut);

                if (days <= 0) {
                    System.out.println("Check-out date must be after check-in date.");
                    return;
                }

                for (Room room : rooms) {
                    if (room.getCategory() == category && room.isAvailable()) {
                        room.setAvailable(false);
                        String bookingId = generateBookingId();
                        double totalAmount = room.getPricePerNight() * days;

                        Reservation reservation = new Reservation(
                                bookingId, name, phone, room.getRoomId(), category,
                                checkIn, checkOut, days, totalAmount, PaymentStatus.PENDING
                        );

                        reservations.add(reservation);
                        saveRoomsToFile();
                        saveReservationsToFile();

                        System.out.println("\nBooking successful.");
                        System.out.println(reservation);
                        return;
                    }
                }

                System.out.println("No rooms available in selected category.");
            } catch (Exception e) {
                System.out.println("Booking failed. Invalid input.");
            }
        }

        public void cancelReservation(String bookingId) {
            for (Reservation reservation : reservations) {
                if (reservation.getBookingId().equalsIgnoreCase(bookingId)) {
                    if (reservation.getPaymentStatus() == PaymentStatus.CANCELLED) {
                        System.out.println("Reservation is already cancelled.");
                        return;
                    }

                    reservation.setPaymentStatus(PaymentStatus.CANCELLED);

                    for (Room room : rooms) {
                        if (room.getRoomId().equalsIgnoreCase(reservation.getRoomId())) {
                            room.setAvailable(true);
                            break;
                        }
                    }

                    saveRoomsToFile();
                    saveReservationsToFile();
                    System.out.println("Reservation cancelled successfully.");
                    return;
                }
            }
            System.out.println("Booking ID not found.");
        }

        public void processPayment(String bookingId, Scanner sc) {
            for (Reservation reservation : reservations) {
                if (reservation.getBookingId().equalsIgnoreCase(bookingId)) {
                    if (reservation.getPaymentStatus() == PaymentStatus.CANCELLED) {
                        System.out.println("Cannot process payment for cancelled reservation.");
                        return;
                    }

                    if (reservation.getPaymentStatus() == PaymentStatus.PAID) {
                        System.out.println("Payment already completed.");
                        return;
                    }

                    System.out.println("Total amount to pay: Rs." + reservation.getTotalAmount());
                    System.out.print("Enter amount paid: Rs.");
                    double paid = Double.parseDouble(sc.nextLine());

                    if (paid < reservation.getTotalAmount()) {
                        System.out.println("Insufficient amount. Payment failed.");
                        return;
                    }

                    reservation.setPaymentStatus(PaymentStatus.PAID);
                    saveReservationsToFile();

                    double change = paid - reservation.getTotalAmount();
                    System.out.println("Payment successful.");
                    System.out.println("Change: Rs." + change);
                    return;
                }
            }
            System.out.println("Booking ID not found.");
        }

        public void viewBookingDetails(String bookingId) {
            for (Reservation reservation : reservations) {
                if (reservation.getBookingId().equalsIgnoreCase(bookingId)) {
                    System.out.println("\n===== BOOKING DETAILS =====");
                    System.out.println(reservation);
                    return;
                }
            }
            System.out.println("Booking ID not found.");
        }

        public void viewAllReservations() {
            if (reservations.isEmpty()) {
                System.out.println("No reservations found.");
                return;
            }

            System.out.println("\n===== ALL RESERVATIONS =====");
            for (Reservation reservation : reservations) {
                System.out.println(reservation);
            }
        }

        private String generateBookingId() {
            return "B" + (1000 + reservations.size() + 1);
        }

        private RoomCategory readCategory(Scanner sc) {
            while (true) {
                System.out.println("Select room category:");
                System.out.println("1. STANDARD");
                System.out.println("2. DELUXE");
                System.out.println("3. SUITE");
                System.out.print("Enter choice: ");

                String input = sc.nextLine();

                switch (input) {
                    case "1":
                        return RoomCategory.STANDARD;
                    case "2":
                        return RoomCategory.DELUXE;
                    case "3":
                        return RoomCategory.SUITE;
                    default:
                        System.out.println("Invalid category. Try again.");
                }
            }
        }
    }

    public static void main(String[] args) {
        Scanner sc = new Scanner(System.in);
        HotelManager manager = new HotelManager();

        while (true) {
            System.out.println("\n========= HOTEL RESERVATION SYSTEM =========");
            System.out.println("1. View All Rooms");
            System.out.println("2. Search Room by Category");
            System.out.println("3. Book Room");
            System.out.println("4. Cancel Reservation");
            System.out.println("5. Make Payment");
            System.out.println("6. View Booking Details");
            System.out.println("7. View All Reservations");
            System.out.println("8. Exit");
            System.out.print("Enter your choice: ");

            String choice = sc.nextLine();

            switch (choice) {
                case "1":
                    manager.viewAllRooms();
                    break;
                case "2":
                    System.out.println("Choose category:");
                    System.out.println("1. STANDARD");
                    System.out.println("2. DELUXE");
                    System.out.println("3. SUITE");
                    System.out.print("Enter choice: ");
                    String categoryChoice = sc.nextLine();

                    switch (categoryChoice) {
                        case "1":
                            manager.searchRooms(RoomCategory.STANDARD);
                            break;
                        case "2":
                            manager.searchRooms(RoomCategory.DELUXE);
                            break;
                        case "3":
                            manager.searchRooms(RoomCategory.SUITE);
                            break;
                        default:
                            System.out.println("Invalid category choice.");
                    }
                    break;
                case "3":
                    manager.bookRoom(sc);
                    break;
                case "4":
                    System.out.print("Enter booking ID to cancel: ");
                    manager.cancelReservation(sc.nextLine());
                    break;
                case "5":
                    System.out.print("Enter booking ID for payment: ");
                    manager.processPayment(sc.nextLine(), sc);
                    break;
                case "6":
                    System.out.print("Enter booking ID to view: ");
                    manager.viewBookingDetails(sc.nextLine());
                    break;
                case "7":
                    manager.viewAllReservations();
                    break;
                case "8":
                    System.out.println("Thank you for using Hotel Reservation System.");
                    sc.close();
                    return;
                default:
                    System.out.println("Invalid choice. Please try again.");
            }
        }
    }
}