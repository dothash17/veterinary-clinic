import lombok.Getter;
import lombok.Setter;
import java.text.SimpleDateFormat;
import java.util.*;

public class App {
    public static void main(String[] args) {
        AppStart.start();
    }

    public static class Storage {
        public static List<Patient> patients = new ArrayList<>();
        public static List<Doctor> doctors = new ArrayList<>();
        public static List<Appointment> appointments = new ArrayList<>();
    }

    @Getter
    @Setter
    public static class Appointment {

        private UUID id = UUID.randomUUID();

        private String patientFio;
        private Calendar registrationDate;

        private AppointmentStatus status;

        public String getRegistrationDate(){
            SimpleDateFormat sdf = new SimpleDateFormat("yyyy-MM-dd");
            return sdf.format(registrationDate.getTime());
        }
        public Appointment(String patientFio, Calendar registrationDate,AppointmentStatus status){

            this.patientFio = patientFio;
            this.registrationDate = registrationDate;
            this.status = status;
        }
    }

    @Getter
    @Setter
    public static class Doctor {
        private UUID id = UUID.randomUUID();

        private String fio;

        public Doctor(String fio) {
            this.fio = fio;
        }
    }

    @Getter
    @Setter
    public static class Patient {

        private UUID id = UUID.randomUUID();

        private String fio;

        private Calendar registrationDate;

        public String getRegistrationDate(){
            SimpleDateFormat sdf = new SimpleDateFormat("yyyy-MM-dd");
            return sdf.format(registrationDate.getTime());
        }

        public Patient(String fio, Calendar registrationDate) {

            this.fio = fio;
            this.registrationDate = registrationDate;
        }
    }

    public enum AppointmentStatus {
        NEW("NEW"),
        IN_PROCESS("IN-PROCESS"),
        CANCELLED("CANCELLED"),
        AWAITING_PAYMENT("AWAITING-PAYMENT"),
        COMPLETED("COMPLETED");

        private final String text;

        AppointmentStatus(String text) {
            this.text = text;
        }

        public String toString() {
            return text;
        }
    }

    public static class AppointmentServiceImpl {

        public void createAppointment() {

            Scanner scanner = new Scanner(System.in);

            System.out.print("Введите ФИО: ");
            String patientFio = scanner.nextLine();
            System.out.print("Введите год: ");
            int year = scanner.nextInt();
            System.out.print("Введите месяц: ");
            int month = scanner.nextInt();
            System.out.print("Введите день: ");
            int day = scanner.nextInt();
            Calendar registrationDate = new GregorianCalendar(year, month, day);

            Appointment appointment = new Appointment(patientFio, registrationDate, AppointmentStatus.NEW);

            Storage.appointments.add(appointment);
        }

        public void showAllPatientAppointments() {
            Scanner scanner = new Scanner(System.in);

            System.out.print("Введите ФИО пациента: ");
            String patientFio = scanner.nextLine();

            if (Storage.appointments.stream().noneMatch(a -> a.getPatientFio().equals(patientFio))) {
                System.out.println("Этот пациент не записан на прием");
                return;
            }

            System.out.println("Список назначенных приемов для " + patientFio);
            Storage.appointments.forEach(a -> {
                if (a.getPatientFio().equals(patientFio))
                    System.out.println(a.getRegistrationDate() + " " + a.getStatus().toString());
            });

        }

        public void changeAppointmentStatus() {
            Scanner scanner = new Scanner(System.in);

            System.out.print("Enter appointment id: ");
            UUID appointmentId = UUID.fromString(scanner.nextLine());

            System.out.print("""
                Enter new status:
                0 - NEW
                1 - IN PROCESS
                2 - CANCELLED
                3 - AWAITING PAYMENT
                4 - COMPLETED
                """);

            int statusCode = scanner.nextInt();
            AppointmentStatus newStatus = null;

            switch (statusCode) {
                case 0 -> newStatus = AppointmentStatus.NEW;
                case 1 -> newStatus = AppointmentStatus.IN_PROCESS;
                case 2 -> newStatus = AppointmentStatus.CANCELLED;
                case 3 -> newStatus = AppointmentStatus.AWAITING_PAYMENT;
                case 4 -> newStatus = AppointmentStatus.COMPLETED;
            }

            Storage.appointments.stream().filter(a -> a.getId().equals(appointmentId)).findFirst().get().setStatus(newStatus);

        }
    }

    public static class DoctorServiceImpl {

        public void createDoctor() {
            Scanner scanner = new Scanner(System.in);
            System.out.print("Введите ФИО: ");
            String fio = scanner.nextLine();

            Storage.doctors.add(new Doctor(fio));
            System.out.println("Врач " + fio + " добавлен!");
        }
    }

    public static class PatientServiceImpl {

        public void createPatient() {
            Scanner scanner = new Scanner(System.in);
            System.out.print("Введите ФИО: ");
            String fio = scanner.nextLine();
            System.out.print("Введите год: ");
            int year = scanner.nextInt();
            System.out.print("Введите месяц: ");
            int month = scanner.nextInt();
            System.out.print("Введите день: ");
            int day = scanner.nextInt();
            Calendar registrationDate = new GregorianCalendar(year, month, day);

            Storage.patients.add(new Patient(fio, registrationDate));
            System.out.println("Пациент добавлен!");
        }

        public void showAllPatients() {
            if (Storage.patients.isEmpty()) {
                System.out.println("Нет пациентов");
                return;
            }

            Storage.patients.forEach(p -> {
                System.out.printf("ID: %s\nФИО: %s\nДата регистрации: %s", p.getId(), p.getFio(), p.getRegistrationDate());
                System.out.println("\n");
            });
        }

        public void deleteUser() {
            Scanner scanner = new Scanner(System.in);
            System.out.print("Введите id: ");
            UUID patientId = UUID.fromString(scanner.nextLine());
            Storage.patients.removeIf(p -> p.getId().equals(patientId));

            System.out.println("Пользователь удален!");
        }

        public void editPatientFio() {
            Scanner scanner = new Scanner(System.in);

            System.out.print("Введите id: ");
            UUID patientId = UUID.fromString(scanner.nextLine());
            Patient patient = Storage.patients.stream().filter(p -> p.getId().equals(patientId)).findFirst().get();


            System.out.print("Введите ФИО: ");
            String fio = scanner.nextLine();

            patient.setFio(fio);
        }
    }

    public static class Authentication {
        public static final String LOGIN = "admin";
        public static final String PASSWORD = "1234";

        public static boolean auth() {
            Scanner scanner = new Scanner(System.in);
            System.out.print("Введите логин: ");
            String login = scanner.nextLine();
            System.out.print("Введите пароль: ");
            String password = scanner.nextLine();

            return login.equals(LOGIN) & password.equals(PASSWORD);
        }
    }

    public class AppStart {
        private static final PatientServiceImpl patientService = new PatientServiceImpl();
        private static final DoctorServiceImpl doctorService = new DoctorServiceImpl();
        private static final AppointmentServiceImpl appointmentService = new AppointmentServiceImpl();

        public static void start() {
            Scanner scanner = new Scanner(System.in);
            if (Authentication.auth()) while (true) {
                System.out.print("""
                    0 - Закрыть
                    1 - Создать пациента
                    2 - Создать врача
                    3 - Создать прием
                    4 - Удалить пациента
                    5 - Показать всех пациентов
                    6 - Редактировать ФИО пациента
                    7 - Показать все приемы пациента
                    8 - Изменить статус приема
                    """);
                switch (scanner.nextInt()) {
                    case 0 -> System.exit(0);
                    case 1 -> patientService.createPatient();
                    case 2 -> doctorService.createDoctor();
                    case 3 -> appointmentService.createAppointment();
                    case 4 -> patientService.deleteUser();
                    case 5 -> patientService.showAllPatients();
                    case 6 -> patientService.editPatientFio();
                    case 7 -> appointmentService.showAllPatientAppointments();
                    case 8 -> appointmentService.changeAppointmentStatus();
                }
            }
            else {
                System.out.println("Некорректно!");
            }
        }
    }
}

