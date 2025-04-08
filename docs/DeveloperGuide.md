---
  layout: default.md
  title: "Developer Guide"
  pageNav: 3
---
<img src="images/Klinix_logo.png" alt="klinix-logo" style="pointer-events: none; height: 120px; width: auto; display: block; margin: 0 auto;">

# **Klinix Developer Guide**

<!-- * Table of Contents -->
<page-nav-print />

--------------------------------------------------------------------------------------------------------------------

## **Acknowledgements**

This project is based on the AddressBook-Level 3 project created by the [SE-EDU initiative](https://se-education.org/)

--------------------------------------------------------------------------------------------------------------------

## **Setting up, getting started**

Refer to the guide [_Setting up and getting started_](SettingUp.md).

--------------------------------------------------------------------------------------------------------------------

## **Design**

### Architecture

<puml src="diagrams/ArchitectureDiagram.puml" width="280"></puml>

The ***Architecture Diagram*** given above explains the high-level design of the App.

Given below is a quick overview of main components and how they interact with each other.

**Main components of the architecture**


**`Main`** (consisting of classes [`Main`](https://github.com/AY2425S2-CS2103T-T09-2/tp/blob/master/src/main/java/seedu/address/Main.java) and [`MainApp`](https://github.com/AY2425S2-CS2103T-T09-2/tp/blob/master/src/main/java/seedu/address/MainApp.java)) is in charge of the App launch and shut down.
* At App launch, it initializes the other components in the correct sequence, and connects them up with each other.
* At shut down, it shuts down the other components and invokes cleanup methods where necessary.

The bulk of the App's work is done by the following four components:

* [**`UI`**](#ui-component): The UI of the App.
* [**`Logic`**](#logic-component): The command executor.
* [**`Model`**](#model-component): Holds the data of the App in memory.
* [**`Storage`**](#storage-component): Reads data from, and writes data to, the hard disk.

[**`Commons`**](#common-classes) represents a collection of classes used by multiple other components.

**How the architecture components interact with each other**

The *Sequence Diagram* below shows how the components interact with each other for the scenario where the user issues the command `delete 1`.

<puml src="diagrams/ArchitectureSequenceDiagram.puml" width="574" />

Each of the four main components (also shown in the diagram above),

* defines its *API* in an `interface` with the same name as the Component.
* implements its functionality using a concrete `{Component Name}Manager` class (which follows the corresponding API `interface` mentioned in the previous point.

For example, the `Logic` component defines its API in the `Logic.java` interface and implements its functionality using the `LogicManager.java` class which follows the `Logic` interface. Other components interact with a given component through its interface rather than the concrete class (reason: to prevent outside component's being coupled to the implementation of a component), as illustrated in the (partial) class diagram below.

<puml src="diagrams/ComponentManagers.puml" width="300" />

The sections below give more details of each component.

### UI component

The **API** of this component is specified in [`Ui.java`](https://github.com/AY2425S2-CS2103T-T09-2/tp/blob/master/src/main/java/seedu/address/ui/Ui.java)

<puml src="diagrams/UiClassDiagram.puml" alt="Structure of the UI Component"/>

The UI consists of a `MainWindow` that is made up of parts, e.g.`CommandBox`, `ResultDisplay`, `PersonListPanel`, `AppointmentListPanel`, `StatusBarFooter` etc. All these, including the `MainWindow`, inherit from the abstract `UiPart` class which captures the commonalities between classes that represent parts of the visible GUI.

The `UI` component uses the JavaFX UI framework. The layout of these UI parts are defined in matching `.fxml` files that are in the `src/main/resources/view` folder. For example, the layout of the [`MainWindow`](https://github.com/se-edu/addressbook-level3/tree/master/src/main/java/seedu/address/ui/MainWindow.java) is specified in [`MainWindow.fxml`](https://github.com/se-edu/addressbook-level3/tree/master/src/main/resources/view/MainWindow.fxml)

The `UI` component,

* executes user commands using the `Logic` component.
* listens for changes to `Model` data so that the UI can be updated with the modified data.
* keeps a reference to the `Logic` component, because the `UI` relies on the `Logic` to execute commands.
* depends on some classes in the `Model` component, as it displays `Person` and `Appointment` objects residing in the `Model`.

### Logic component

**API** : [`Logic.java`](https://github.com/AY2425S2-CS2103T-T09-2/tp/blob/master/src/main/java/seedu/address/logic/Logic.java)

Here's a (partial) class diagram of the `Logic` component:

<puml src="diagrams/LogicClassDiagram.puml" width="550"/>

The sequence diagram below illustrates the interactions within the `Logic` component, taking `execute("delete 1")` API call as an example.

<puml src="diagrams/DeleteSequenceDiagram.puml" alt="Interactions Inside the Logic Component for the `delete 1` Command" />

<box type="info" seamless>

**Note:** The lifeline for `DeleteCommandParser` should end at the destroy marker (X) but due to a limitation of PlantUML, the lifeline continues till the end of diagram.
</box>

How the `Logic` component works:

1. When `Logic` is called upon to execute a command, it is passed to a `KlinixParser` object which in turn creates a parser that matches the command (e.g., `DeleteCommandParser`) and uses it to parse the command.
1. This results in a `Command` object (more precisely, an object of one of its subclasses, e.g., `DeleteCommand`) which is executed by the `LogicManager`.
1. The command can communicate with the `Model` when it is executed (e.g., to delete a person).<br>
   Note that although this is shown as a single step in the diagram above (for simplicity), in the code it can take several interactions (between the command object and the `Model`) to achieve.
1. The result of the command execution is encapsulated as a `CommandResult` object which is returned from `Logic`.

Here are the other classes in `Logic` (omitted from the class diagram above) that are used for parsing a user command:

<puml src="diagrams/ParserClasses.puml" width="600"/>

How the parsing works:
* When called upon to parse a user command, the `KlinixParser` class creates an `XYZCommandParser` (`XYZ` is a placeholder for the specific command name e.g., `AddCommandParser`) which uses the other classes shown above to parse the user command and create a `XYZCommand` object (e.g., `AddCommand`) which the `KlinixParser` returns back as a `Command` object.
* All `XYZCommandParser` classes (e.g., `AddCommandParser`, `DeleteCommandParser`, ...) inherit from the `Parser` interface so that they can be treated similarly where possible e.g, during testing.

### Model component
**API** : [`Model.java`](https://github.com/AY2425S2-CS2103T-T09-2/tp/blob/master/src/main/java/seedu/address/model/Model.java)

<puml src="diagrams/ModelClassDiagram.puml" width="450" />


The `Model` component,

* stores the address book data, i.e., all `Person` objects (which are contained in a `UniquePersonList` object).
* stores the currently 'selected' `Person` objects (e.g., results of a search query) as a separate _filtered_ list which is exposed to outsiders as an unmodifiable `ObservableList<Person>` that can be 'observed', e.g., the UI can be bound to this list so that the UI automatically updates when the data in the list changes.
* stores a `UserPref` object that represents the user’s preferences. This is exposed to the outside as a `ReadOnlyUserPref` object.
* does not depend on any of the other three components (as the `Model` represents data entities of the domain, they should make sense on their own without depending on other components)

### Storage component

**API** : [`Storage.java`](https://github.com/AY2425S2-CS2103T-T09-2/tp/blob/master/src/main/java/seedu/address/storage/Storage.java)

<puml src="diagrams/StorageClassDiagram.puml" width="550" />

The `Storage` component,
* can save both address book data and user preference data in JSON format, and read them back into corresponding objects.
* inherits from both `KlinixStorage` and `UserPrefStorage`, which means it can be treated as either one (if only the functionality of only one is needed).
* depends on some classes in the `Model` component (because the `Storage` component's job is to save/retrieve objects that belong to the `Model`)

### Person class
The `person` class represents a patient in Klinix.

<puml src="diagrams/PersonClassDiagram.puml" width="550" />

### Common classes

Classes used by multiple components are in the `seedu.address.commons` package.

--------------------------------------------------------------------------------------------------------------------

## **Implementation**

This section describes some noteworthy details on how certain features are implemented.

### Add Appointment Feature

#### Overview
The `addappt` command allows a user to add an appointment tied to a patient with the specified NRIC. The command requires:
- **NRIC** – Patient's NRIC in Klinix.
- **Description** – Description of the appointment.
- **Start Date** – Starting date and time of the appointment.
- **End Date** – Ending date and time of the appointment.

Here is a sequence diagram showcasing the flow of the program as well as the key steps taken.

<puml src="diagrams/AddAppointmentSequenceDiagram.puml" alt="AddAppointmentSequenceDiagram" />

#### 1. Parsing User Input
The **`AddAppointmentCommandParser`** class is responsible for parsing user input. It uses `ArgumentTokenizer` to tokenize the input string, extracting:
- **NRIC** – Identifies the patient in Klinix.
- **Description** – Details about the appointment.
- **Start Date** – Start of the appointment.
- **End Date** – End of the appointment.

During this parsing process:
- An `Appointment` instance is created to hold the appointment details.

#### 2. Executing the Command
The **`AddAppointmentCommand`** class performs the following steps to add an appointment:

1. **Retrieves Patient Information**:
   - Uses the NRIC from the parser to locate the patient.

2. **Create a new `Person` instance with the appointment added to the appointment list**:
   - Utilises patient information from the current patient (identified by the NRIC) and the new `Appointment` details.
   - Creates a new `Person` instance with patient information and `Appointment` instance.

3. **Replace the existing patient record**:
   - The new `Person` instance containing the `Appointment` replaces the existing patient record in the `Model`.

#### 3. Handling Invalid Inputs
The **`AddAppointmentCommandParser`** and **`AddAppointmentCommand`** classes enforce validation rules to ensure correct formats and scheduling logic:

- **Format Verification**:
   - **`AddAppointmentCommandParser`** checks if the date and time format follows `dd-MM-yyyy HH:mm`.
   - It checks if the date and time are valid.
   - It also ensures the **Start Date** is before or equal to the **End Date**.
   - **`AddAppointmentCommandParser`** also checks if the NRIC of the patient follows its format and if the description of the appointment follows its format.
     <br><br>

- **Conflict Checking**:
   - **`AddAppointmentCommand`** checks if the new appointment to be added overlaps with any existing appointments for the patient or other patients.
   - If there is an overlap for any of these scenarios, an error message is thrown, preventing the appointment from being created.
   - If no overlap exists, the new appointment is added to the appointment list of the patient.

### View Appointment by Date command

#### Overview

The `appton` command allows a user to view all appointments that start on that date. The command requires:
- **DATE** – Date on which the appointments start.

Here is a sequence diagram showcasing the flow of the program as well as the key steps taken.
<puml src="diagrams/ViewAppointmentByDateSequenceDiagram.puml" alt="ViewAppointmentSequenceDiagram" />

#### 1. Parsing User Input
The **`ViewAppointmentByDateParser`** class is responsible for parsing user input. It uses `ArgumentTokenizer` to tokenize the input string, extracting:

- **DATE** – The starting date of the appointments.

- `KlinixParser` creates a new `ViewAppointmentByDateCommand` object, passing the parsed date (e.g., `10-10-2020`) as an argument

#### 2. Executing the Command
The **`ViewAppointmentsByDateCommand`** class performs the following steps to display the filtered appointments for that specific date:

1. **Command creation**:
    - `LogicManager` calls `execute()` on the `ViewAppointmentByDateCommand`.
    - The command interacts with the `Model` to update the displayed appointments for the given date (`10-10-2020`).

2. **Result handling**:
    - The `Model` confirms the update and returns a result to the command.
    - The command forwards this result back to `LogicManager`.

3. **Command cleanup**
    - The `ViewAppointmentByDateCommand` is destroyed after execution.
    - `LogicManager` returns the final result to the user.

#### 3. Handling Invalid Inputs
The **`ViewAppointmentsByDateParser`** class enforces validation rules to ensure correct formats and scheduling logic:

- **Format Verification**:
    - **`ViewAppointmentsByDateParser`** checks if the date format follows `dd-MM-yyyy`.
    - It checks if the date is valid.
      <br><br>
--------------------------------------------------------------------------------------------------------------------

## **Documentation, logging, testing, configuration, dev-ops**

* [Documentation guide](Documentation.md)
* [Testing guide](Testing.md)
* [Logging guide](Logging.md)
* [Configuration guide](Configuration.md)
* [DevOps guide](DevOps.md)

--------------------------------------------------------------------------------------------------------------------

## **Appendix: Requirements**

### Product scope

**Target user profile**:

General Practitioners in smaller clinics

**Value proposition**:
* Helps to keep track of the patients’ records and add details
* Keep track of patients’ medicine needs
* Helps people who type fast
* Ensures accurate patient information handling
* Helps set up and manage appointments with patients
* Powerful search and filtering

### User stories

Priorities: High (must have) - `* * *`, Medium (nice to have) - `* *`, Low (unlikely to have) - `*`

| Priority     | As a ...                            | I want to ...                                                    | So that ...                                                   |
| ------------ | ----------------------------------- |------------------------------------------------------------------|---------------------------------------------------------------|
| `***`    | Basic User                          | add medicine usage records                                       | the data management system remains accurate.                  |
| `***`    | Basic User                          | delete medicine usage records                                    | the data management system remains accurate.                  |
| `***`    | Basic User                          | view detailed patient medical report                             | I have the necessary context during patient check-in.         |
| `***`    | Basic User                          | view patients’ medicine needs                                    | I can manage the clinic’s medicine supply efficiently.        |
| `***`    | Basic User                          | view patient records                                             | I always work with the most current information.              |
| `***`    | Basic User                          | view patient's appointments                                      | I always work with the most current information.              |
| `***`    | Basic User                          | add medical report                                               | the clinic database remains current and accurate.             |
| `***`    | Basic User                          | delete medical report                                            | the clinic database remains current and accurate.             |
| `***`    | Basic User                          | quickly add new patient details                                  | the clinic database remains current and accurate.             |
| `***`    | Basic User                          | quickly delete new patient details                               | the clinic database remains current and accurate.             |
| `***`    | Basic User                          | add appointments                                                 | the clinic database remains current and accurate.             |
| `***`    | Basic User                          | delete appointments                                              | the clinic database remains current and accurate.             |
| `**` | Basic User                          | edit existing patient details                                    | all information stays up-to-date and error-free.              |
| `**` | Basic User                          | schedule new patient appointments                                | clinic visits are well organized.                             |
| `**` | Basic User                          | update medicine usage records                                    | the data management system remains accurate.                  |
| `**` | Advanced User/Fast Typer            | utilize fast-typing shortcuts during data entry                  | I can enter information quickly and reduce wait times.        |
| `**` | Beginner, Basic User, Advanced User | work with an intuitive user interface                            | I need less training and can work more efficiently.           |
| `**` | Basic User, Beginner                | have error-prevention features built into the data entry process | patient information is recorded correctly the first time.     |
| `**` | Basic User                          | confirm any record changes before finalizing them                | I avoid mistakes and ensure accuracy in patient data.         |
| `**` | Basic User                          | search for patients by name                                      | I can quickly narrow down results to find the correct record. |
| `**` | Basic User                          | filter appointments by date                                      | I can efficiently manage busy schedules.                      |
| `**` | Basic User                          | receive alerts for potential double-booking                      | scheduling conflicts are minimized.                           |
| `**` | Basic User, Beginner                | navigate the application easily                                  | I can quickly access the functionality I need most often.     |
| `**` | Forgetful User                      | mark and unmark patients as “visited” after their appointments   | follow-ups and further actions can be tracked efficiently.    |
| `**` | Beginner                            | view the user guide easily                                       | I can learn more about the product as and when I need         |

### Use cases
The use cases below are not exhaustive.
(For all use cases below, the **System** is the `Klinix` and the **Actor** is the `user`, unless specified otherwise)

**Use case: Adding a Patient**

**MSS**
1. User requests to add a new patient.
2. Klinix confirms successful patient registration.<br>
   Use case ends.

**Extensions**
- 1a. Missing required parameters → Klinix shows an invalid command error message.<br>
  Use case resumes at step 1.

- 1b. Invalid parameters → Klinix shows an invalid parameter message.<br>

  Use case resumes at step 1.
- 1c. Patient with existing NRIC → Klinix shows an error message indicating the NRIC exists in Klinix.<br>
  Use case resumes at step 1.

---

**Use case: Deleting a Patient**

**Precondition**: Klinix is displaying a non-empty list of patients.

**Format 1: Deleting by NRIC**

**MSS**
1. User requests to delete a patient by NRIC.
2. Klinix confirms successful deletion of patient.<br>
   Use case ends.

**Extensions**
- 1a. Missing NRIC → Klinix shows an invalid command error message.<br>
  Use case resumes at step 1.
- 1b. Invalid NRIC format → Klinix shows an invalid parameter error message.<br>
  Use case resumes at step 1.
- 1c. No matching patient found → Klinix shows an invalid patient error message<br>
  Use case resumes at step 1.

**Format 2: Deleting by index**

**MSS**
1. User requests to delete a patient by index.
2. Klinix confirms successful deletion of patient.<br>
   Use case ends.

**Extensions**
- 1a. Missing index → Klinix shows an invalid command error message.<br>
  Use case resumes at step 1.
- 1b. Invalid index format → Klinix shows an invalid parameter error message.<br>
  Use case resumes at step 1.
- 1c. Patient index out of bounds → Klinix shows an error message.<br>
  Use case resumes at step 1.

---
**Use case: Editing a Patient**

**MSS**
1. User requests to edit a patient by index.
2. System displays information of updated patient.<br>
   Use case ends.

**Extensions**
- 1a. Missing parameters → Klinix shows an invalid command error message<br>
  Use case resumes at step 1.
- 1b. Invalid parameters format → Klinix shows an invalid parameters error message.<br>
  Use case resumes at step 1.
- 1c. Patient index out of bounds → Klinix shows an error message.<br>
  Use case resumes at step 1.

---

**Use case: Finding Patients**

**MSS**
1. User finds the patients by keywords, only name is searched.
2. Klinix shows a list of patients matching the search.<br>
   Use case ends.

**Extensions**
- 1a. Missing name → Klinix shows an invalid command error message.<br>
  Use case resumes at step 1.
- 1b. No matching patient found → Klinix shows an empty list.<br>
  Use case ends.

---

**Use case: Clearing all Patient Entries**

**MSS**
1. User requests to clear the list of patients.
2. Klinix clears the list of patients.<br>
   Use case ends.

---

**Use case: Adding a Medical Report**

**MSS**
1. User requests to add a medical report for a patient. 
2. Klinix confirms successful addition of medical report.<br>
   Use case ends.

**Extensions**
- 1a. Missing NRIC → Klinix shows an invalid command error message.<br>
  Use case resumes at step 1.
- 1b. Invalid parameters format → Klinix shows an invalid parameters error message.<br>
  Use case resumes at step 1.
- 1c. No matching patient found → Klinix shows an invalid patient error message.<br>
  Use case resumes at step 1.
- 1d. No medical fields provided → Klinix shows an invalid fields message.<br>
  Use case resumes at step 1.

---

**Use case: Deleting a Medical Report**

**Precondition**: Klinix is displaying a non-empty list of patients.

**Format 1: Deleting by NRIC**

**MSS**
1. User requests to delete a medical report by NRIC.
2. Klinix confirms successful deletion of medical report.<br>
   Use case ends.

**Extensions**
- 1a. Missing NRIC → Klinix shows an invalid command error message.<br>
  Use case resumes at step 1.
- 1b. Invalid NRIC format → Klinix shows an invalid parameter error message.<br>
  Use case resumes at step 1.
- 1c. No matching patient found → Klinix shows an invalid patient error message<br>
  Use case resumes at step 1.
- 1d. No medical report found → Klinix shows Patient does not have medical report error message<br>
  Use case resumes at step 1.

**Format 2: Deleting by index**

**MSS**
1. User requests to delete a patient by index.
2. Klinix confirms successful deletion of medical report.<br>
   Use case ends.

**Extensions**
- 1a. Missing index → Klinix shows an invalid command error message.<br>
  Use case resumes at step 1.
- 1b. Invalid index format → Klinix shows an invalid parameter error message.<br>
  Use case resumes at step 1.
- 1c. Patient index out of bounds → Klinix shows an error message.<br>
  Use case resumes at step 1.
- 1d. No medical report found → Klinix shows Patient does not have medical report error message<br>
  Use case resumes at step 1.

---

**Use case: Adding a Medicine Usage Record**

**MSS**
1. User requests to add a medicine usage record for a patient.
2. Klinix confirms successful addition of medicine usage record<br>
   Use case ends.

**Extensions**
- 1a. Missing parameters → Klinix shows an invalid command error message.<br> 
  Use case resumes at step 1.
- 1b. Invalid parameters format → Klinix shows an invalid parameters error message.<br>
  Use case resumes at step 1.
- 1c. Duplicate entry detected → Klinix shows a duplicate entry error message.<br>
  Use case resumes at step 1.
- 1d. Start date is after end date → Klinix shows an invalid date error message.<br>
  Use case resumes at step 1.

---

**Use case: Deleting a Medicine Usage Record**

**MSS**
1. User requests to delete a medicine usage by NRIC.
2. Klinix confirms successful deletion of medicine usage.<br>
   Use case ends.

**Extensions**
- 1a. Missing parameters → Klinix shows an invalid command error message.<br>
  Use case resumes at step 1.
- 1b. Invalid parameters format → Klinix shows an invalid parameters error message.<br>
  Use case resumes at step 1.
- 1c. No matching patient found → Klinix shows an invalid patient error message<br>
  Use case resumes at step 1.
- 1d. Medicine usage record index out of bounds → Klinix shows an error message.<br>
  Use case resumes at step 1.

---

**Use case: Clearing Medicine Usage Records**

**Preconditions**: Klinix is displaying a non-empty list of patients and the patient has a non-empty list of medicine usage records.

**Format 1: Clearing by NRIC**

**MSS**
1. User requests to clear the list of medicine usages by NRIC.
2. Klinix confirms successful deletion of patient's list of medicine usage.<br>
   Use case ends.

**Extensions**
- 1a. Missing NRIC → Klinix shows an invalid command message.<br>
  Use case resumes at step 1.
- 1b. Invalid NRIC format → Klinix shows an invalid parameter message.<br>
  Use case resumes at step 1.
- 1c. No matching patient found → Klinix shows an invalid patient message<br>
  Use case resumes at step 1.

**Format 2: Clearing by index**

**MSS**
1. User requests to delete all medicine usages records from the patient found by index.
2. Klinix confirms successful deletion of medicine usage records.<br>
   Use case ends.

**Extensions**
- 1a. Missing index → Klinix shows an invalid command error message.<br>
  Use case resumes at step 1.
- 1b. Invalid index format → Klinix shows an invalid parameter error message.<br>
  Use case resumes at step 1.
- 1c. Patient index out of bounds → Klinix shows an error message.<br>
  Use case resumes at step 1.

---

**Use case: Finding Medicine Usage Records**
**MSS**
1. User searches medicine usage records by keywords.
2. Klinix shows a list of patients with medicine usage records matching the search.<br>
   Use case ends.

**Extensions**
- 1a. Missing keywords → Klinix shows an invalid command error message.<br>
  Use case resumes at step 1.
- 1b. No matching medicine usage records found → Klinix shows an empty list.<br>
  Use case ends.

---

**Use case: Adding an Appointment**

**MSS**
1. User requests to add an appointment for a patient. 
2. Klinix confirms successful addition of appointment.<br>
   Use case ends.

**Extensions**
- 1a. Missing parameters → Klinix shows an invalid command error message.<br>
  Use case resumes at step 1.
- 1b. Invalid parameters format → Klinix shows an invalid parameters error message.<br>
  Use case resumes at step 1.
- 1c. No matching patient found → Klinix shows an invalid patient error message<br>
  Use case resumes at step 1.
- 1d. Overlapping appointment detected → Klinix shows overlapping appointments error message.<br>
  Use case resumes at step 1.
- 1e. Invalid date format → Klinix shows an invalid date error message.<br>
  Use case resumes at step 1.

---

**Use case: Deleting an Appointment**

**MSS**
1. User requests to delete an appointment by NRIC.
2. Klinix confirms successful deletion of appointment.<br>
   Use case ends.

**Extensions**
- 1a. Missing parameters → Klinix shows an invalid command error message.<br>
  Use case resumes at step 1.
- 1b. Invalid parameters format → Klinix shows an invalid parameters error message.<br>
  Use case resumes at step 1.
- 1c. No matching patient found → Klinix shows an invalid patient error message<br>
  Use case resumes at step 1.
- 1d. Appointment index out of bounds → Klinix shows an error message.<br>
  Use case resumes at step 1.
---

**Use case: Clearing Appointments**

**Preconditions**: Klinix is displaying a non-empty list of patients and the patient is displaying a non-empty list of appointments.

**Format 1: Delete by NRIC**

**MSS**
1. User requests to clear list of patient's appointments by NRIC.
2. Klinix confirms successful deletion of patient's list of appointments.<br>
   Use case ends.

**Extensions**
- 1a. Missing NRIC → Klinix shows an invalid command error message.<br>
  Use case resumes at step 1.
- 1b. Invalid NRIC format → Klinix shows an invalid parameter error message.<br>
  Use case resumes at step 1.
- 1c. No matching patient found → Klinix shows an invalid patient error message<br>
  Use case resumes at step 1.

**Format 2: Delete by index**

**Preconditions**: Klinix is displaying a non-empty list of patients.

**MSS**
1. User requests to clear list of patient's appointments by index.
2. Klinix confirms successful deletion of patient's list of appointments.<br>
   Use case ends.

**Extensions**
- 1a. Missing index → Klinix shows an invalid command error message.<br>
  Use case resumes at step 1.
- 1b. Invalid parameter format → Klinix shows an invalid parameter error message.<br>
  Use case resumes at step 1.
- 1c. Patient index out of bounds → Klinix shows an error message.<br>
  Use case resumes at step 1.

---

**Use Case: View Patient details**

**MSS**
1. User requests to view a patient’s details by clicking on the list of patients.
2. Klinix displays details of the patient.
   Use case ends.

---

**Use Case: View Appointments on Date**

**MSS**
1. User requests to view appointments for a date.
2. Klinix retrieves and displays all appointments which start on that date.<br>
  Use case ends.

**Extensions**
- 1a. Missing parameters → Klinix shows an invalid command error message.<br>
  Use case resumes at step 1.
- 1b. Invalid date parameters → Klinix shows an invalid date error message.<br>
  Use case resumes at step 1.
- 1c. No appointments with same starting date → Klinix shows a message indicating no appointments were found.
  Use case ends

---

**Use Case: Mark Appointment**

**MSS**
1. User requests to mark an appointment as visited.
2. Klinix confirms successful marking of patient's appointment as visited.<br>
   Use case ends.

**Extensions**
- 1a. Missing parameters → Klinix shows an invalid command error message.<br>
  Use case resumes at step 1.
- 1b. Invalid parameters format → Klinix shows an invalid parameters error message.<br>
  Use case resumes at step 1.
- 1c. No matching patient found → Klinix shows an invalid patient error message<br>
  Use case resumes at step 1.
- 1d. Appointment index out of bounds → Klinix shows an error message.<br>
  Use case resumes at step 1.
- 1c. Appointment already marked visited → Klinix shows a message indicating the appointment is already marked visited.<br>
  Use case ends.

---

**Use Case: Unmark Appointment**

**MSS**
1. User requests to mark an appointment as not visited.
2. Klinix confirms successful marking of patient's appointment as not visited.<br>
   Use case ends.

**Extensions**
- 1a. Missing parameters → Klinix shows an invalid command error message.<br>
  Use case resumes at step 1.
- 1b. Invalid parameters format → Klinix shows an invalid parameters error message.<br>
  Use case resumes at step 1.
- 1c. No matching patient found → Klinix shows an invalid patient error message<br>
  Use case resumes at step 1.
- 1d. Appointment index out of bounds → Klinix shows an error message.<br>
  Use case resumes at step 1.
- Appointment already marked not visited → Klinix shows a message indicating the appointment is already marked not visited.<br>
  User case ends.

---

### Non-Functional Requirements

1.  Should work on any _mainstream OS_ as long as it has Java `17` or above installed.
2.  Should be able to hold up to 1000 patients without a noticeable sluggishness in performance for typical usage.
3.  A user with above average typing speed for regular English text (i.e. not code, not system admin commands) should be able to accomplish most of the tasks faster using commands than using the mouse.

### Glossary

1. **NRIC (National Registration Identity Card)** – A unique identification number assigned to citizens and residents.
2. **Medical Report** – A document containing a patient's medical history, including illnesses, treatments, and surgeries.
3. **Medicine Usage Record** – A record of medications prescribed to a patient, including dosage and duration.
4. **Overlapping Appointment** – When a new appointment conflicts with an existing one in terms of time and date.
5. **Deletion Confirmation** – A message displayed when a record is successfully removed from the system.
6. **Patient**: An individual who receives medical care or consultation from a general practitioner (GP) and has information being managed within Klinix.
7. **MSS (Main Success Scenario)** – The sequence of steps that lead to a successful execution of a use case.
8. **JAR** – A Java Archive file that contains the compiled Java classes and resources for the application.

--------------------------------------------------------------------------------------------------------------------

## **Appendix: Instructions for manual testing**

Given below are instructions to test the App manually.

<box type="info" seamless>

**Note:** These instructions only provide a starting point for testers to work on;
testers are expected to do more *exploratory* testing.

</box>

### Launch and shutdown

1. Initial launch

   1a. Download the jar file and copy into an empty folder

   1b. Double-click the jar file Expected: Shows the GUI with a set of sample contacts. The window size may not be optimal.

2. Saving window preferences

   2a. Resize the window to an optimum size. Move the window to a different location. Close the window.

   2b. Re-launch the App by double-clicking the jar file.<br>
       Expected: The most recent window size and location are retained.

### Adding a Patient

**Command:** `add`<br>

1. Adding a patient with all valid fields

   * **Test case:** `add n/John Doe p/98765432 e/johnd@example.com ic/S0123456Z b/10-10-2000 a/311, Clementi Ave 2, #02-25 t/friends t/owesMoney`<br>
   * **Expected:** Patient is added to the list. Details of the new patient are shown in the status message.
     <br><br>

2. Adding a patient with missing mandatory fields

   * **Test case:** `add n/Lam Tan ic/S1234567Y b/01-01-1990`<br>
   * **Expected:** Patient is not added. Error details shown in the status message.
     <br><br>
3. Adding a patient with invalid NRIC format

   * **Test case:**`add n/Pam Tan ic/1234567X b/01-01-1990`<br>
   * **Expected:** Patient is not added. Error details shown in the status message.
     <br><br>

### Editing a Patient

**Command:** `edit`<br>

1. Editing an existing patient (the first patient in the list)

   * **Prerequisite:**
        * Ensure at least one patient is in the list. If not, add a patient using the `add` command.<br>Eg:
        `add n/John Doe p/98765432 e/johnd@example.com ic/S0123456A b/10-10-2000 a/311, Clementi Ave 2, #02-25 t/friends t/owesMoney`**

   * **Test case:** `edit 1 p/91234567 e/johndoe@example.com`
   * **Expected:** Patient details are updated. Details of the edited patient shown in the status message.
     <br><br>

   * **Test case (invalid email format):** `edit 1 e/wpeodkwoedpk`
   * **Expected:**  Patient details are not updated. Error details shown in the status message.
     <br><br>

2. Editing an invalid patient index

   * **Test case:** `edit 0 p/91234567`<br>
   * **Expected:** Patient details are not updated. Error details shown in the status message.
     <br><br>

3. Invalid edit command

    * **Test case:** `edit 1 wrfwefwefwef`
    * **Expected:** Patient details are not updated. Error details shown in the status message.
      <br><br>

### Deleting a Patient

**Command:** `delete`<br>

1. Deleting a patient using a valid index
    * **Prerequisite:**
        * The patient list contains at least one patient
    * **Test Case:** `delete 1`
    * **Expected:** Klinix deletes the patient at index `1` in the currently displayed patient list.  
      <br><br>

2. Deleting a patient using a valid NRIC
    * **Prerequisite:**
        * The patient with NRIC `S1234567A` exists in the system
    * **Test Case:** `delete ic/S1234567A`
    * **Expected:** Klinix deletes the patient with NRIC `S1234567A` from the system.  
      <br><br>

3. Deleting a patient using an invalid index
    * **Prerequisite:**
        * The patient list has fewer than 99 patients
    * **Test Case:** `delete 99`

    * **Expected:** Klinix shows an error indicating that the index is invalid.
      <br><br>

4. Deleting a patient using an invalid NRIC
    * **Prerequisite:**
        * No patient in the system has NRIC `S9999999Z`
    * **Test Case:** `delete ic/S9999999Z`
    * **Expected:** Klinix shows an error indicating that the patient with NRIC `S9999999Z` does not exist.  
      <br><br>

5. Deleting a patient using both index and NRIC together
    * **Prerequisite:**
        * Any state of the system
    * **Test Case:** `delete 1 ic/S1234567A`
    * **Expected:** Klinix shows an error indicating that the input command format is invalid.  
      <br><br>

6. Deleting a patient with NRIC first and then index
    * **Prerequisite:**
        * Any state of the system
    * **Test Case:** `delete ic/S1234567A 1`
    * **Expected:** Klinix shows an error indicating that the NRIC is invalid, since the index is now counted as part the NRIC.  
      <br><br>

### Adding a Medical Report

**Command:** `addmr`<br>

1. Adding a medical report to a patient
    * **Prerequisite:**
        * The patient with that NRIC must be present in the patient list
    * **Test Case:** `addmr ic/S1234567A al/Peanut Allergy ill/Diabetes sur/Appendectomy imm/Flu Shot, Tetanus`
    * **Expected:** The patient with NRIC S1234567A in the list is updated with the following fields:
        * Drug Allergies: `Peanut Allergy`
        * Illnesses: `Diabetes`
        * Surgeries: `Appendectomy`
        * Immunizations: `Flu Shot, Tetanus`
          <br><br>

2. Adding a medical report to a patient with an existing medical report
   * **Prerequisite:**
        * The patient with that NRIC must be present in the patient list
        * That patient has an existing medical report
    * **Test Case:** `addmr ic/S1234567A al/Peanut Allergy ill/Diabetes sur/Appendectomy imm/Flu Shot, Tetanus`
    * **Expected:** The patient with NRIC S1234567A in the list is updated with the following fields, overwriting the previous fields:
        * Drug Allergies: `Peanut Allergy`
        * Illnesses: `Diabetes`
        * Surgeries: `Appendectomy`
        * Immunizations: `Flu Shot, Tetanus`
          <br><br>

3. Adding a medical report with only some fields
   * **Prerequisite:**
        * The patient with that NRIC must be present in the patient list
    * **Test Case:** `addmr ic/S1234567A al/Peanut Allergy ill/Diabetes`
    * **Expected:** The patient with NRIC S1234567A in the list is updated with the following fields:
        * Drug Allergies: `Peanut Allergy`
        * Illnesses: `Diabetes`
        * Surgeries: `None`
        * Immunizations: `None`
          <br><br>

### Deleting a Medical Report

**Command:** `deletemr`<br>

1. Deleting a medical report from a patient by NRIC
    * **Prerequisite:**
        * The patient with that NRIC must be present in the patient list
        * The patient has at least one medical report entry in their medical report
    * **Test Case:** `deletemr ic/S1234567A`
    * **Expected:** The patient with NRIC S1234567A in the list is updated with the following fields:
        * Drug Allergies: `None`
        * Illnesses: `None`
        * Surgeries: `None`
        * Immunizations: `None`
          <br><br>

2. Deleting a medical report from a patient by index
    * **Prerequisite:**
        * The patient with that NRIC must be present in the patient list
        * The patient has at least one medical report entry in their medical report
    * **Test Case:** `deletemr 1`
    * **Expected:** The patient at index `1` in the currently displayed patient list is updated with the following fields:
        * Drug Allergies: `None`
        * Illnesses: `None`
        * Surgeries: `None`
        * Immunizations: `None`
          <br><br>
      <br><br>

### Adding Medicine Usage Record

**Command:** `addmu`<br>

1. Adding a medicine usage record to a patient
    * **Prerequisite:**
        * The patient with NRIC `S1234567A` is present in the system
    * **Test Case:** `addmu ic/S1234567A n/Paracetamol dos/500mg from/01-01-2025 to/05-01-2025`
    * **Expected:** Klinix adds a new medicine usage to the patient with NRIC `S1234567A` with the following fields:
        * Medicine Name: `Paracetamol`
        * Dosage: `500mg`
        * From: `01-01-2025`
        * To: `05-01-2025`
          <br><br>

2. Adding a medicine usage record to a non-existent patient
    * **Prerequisite:**
        * The patient with NRIC `S9999999Z` is not in the system
    * **Test Case:** `addmu ic/S9999999Z n/Paracetamol dos/500mg from/01-01-2025 to/05-01-2025`
    * **Expected:** Klinix shows an error indicating that the patient with NRIC `S9999999Z` does not exist.  
      <br><br>

3. Adding a medicine usage record with an invalid date format
    * **Prerequisite:**
        * Patient with NRIC `S1234567A` exists
    * **Test Case:** `addmu ic/S1234567A n/Paracetamol dos/500mg from/2025-01-01 to/2025-01-05`
    * **Expected:** Klinix shows an error indicating that the input date format is invalid and must follow the format `dd-MM-yyyy`.  
      <br><br>

4. Adding a medicine usage record with missing required fields
    * **Prerequisite:**
        * Patient with NRIC `S1234567A` exists
    * **Test Case:** `addmu n/Paracetamol dos/500mg from/01-01-2025 to/05-01-2025`
    * **Expected:** Klinix shows an error indicating that the input command is invalid.  
      <br><br>

5. Adding a medicine usage record with duplicate prefixes
    * **Prerequisite:**
        * Patient with NRIC `S1234567A` exists
    * **Test Case:** `addmu ic/S1234567A n/Paracetamol n/Ibuprofen dos/500mg from/01-01-2025 to/05-01-2025`
    * **Expected:** Klinix shows an error indicating that multiple values were provided for the single-valued field `n/`.  
      <br><br>

### Deleting Medicine Usage Record

**Command:** `deletemu`<br>

1. Deleting an existing medicine usage record from a patient
    * **Prerequisites:**
        * The patient with NRIC `S1234567A` is present in the system
        * The patient has at least one medicine usage entry in their medical report
    * **Test Case:** `deletemu 1 ic/S1234567A`
    * **Expected:** Klinix deletes the first medicine usage from the medical report of the patient with NRIC `S1234567A`.  
      <br><br>

2. Deleting a medicine usage record from a non-existing patient
    * **Prerequisite:**
        * The patient with NRIC `S9999999Z` is not in the system
    * **Test Case:** `deletemu 1 ic/S9999999Z`
    * **Expected:** Klinix shows an error indicating that the patient with NRIC `S9999999Z` does not exist.  
      <br><br>

3. Deleting a medicine usage record with an out-of-bounds index
    * **Prerequisite:**
        * The patient with NRIC `S1234567A` is present in the system
        * The patient has fewer than 6 medicine usages
    * **Test Case:** `deletemu 6 ic/S1234567A`
    * **Expected:** Klinix shows an error indicating that the index is invalid.  
      <br><br>

4. Deleting a medicine usage record with an invalid index format
    * **Prerequisite:**
        * Patient with NRIC `S1234567A` exists
    * **Test Case:** `deletemu a ic/S1234567A`
    * **Expected:** Klinix shows an error indicating that the input command is invalid.  
      <br><br>

5. Deleting a medicine usage record without specifying an NRIC
    * **Prerequisite:**
        * At least one patient exists in the system
    * **Test Case:** `deletemu 1`
    * **Expected:** Klinix shows an error indicating that the input command is invalid.  
      <br><br>

6. Deleting a medicine usage record with missing arguments
    * **Prerequisites:**
        * None
    * **Test Case:** `deletemu`
    * **Expected:** Klinix shows an error indicating that the input command is invalid.  
      <br><br>

### Clearing Medicine Usage Records

**Command:** `clearmu`<br>

1. Clearing all medicine usage records using NRIC
    * **Prerequisites:**
        * The patient with NRIC `S1234567A` is present in the system
        * The patient has one or more medicine usage entries in their medical report
    * **Test Case:** `clearmu ic/S1234567A`
    * **Expected:** Klinix clears all medicine usage records from the medical report of the patient with NRIC `S1234567A`.  
      <br><br>

2. Clearing all medicine usage records using an index
    * **Prerequisites:**
        * There is a patient at index `1` in the list
        * That patient has one or more medicine usage entries in their medical report
    * **Test Case:** `clearmu 1`
    * **Expected:** Klinix clears all medicine usage records from the medical report of the patient at index `1`.  
      <br><br>

3. Clearing medicine usage records for a patient with no existing usages (by NRIC)
    * **Prerequisites:**
        * The patient with NRIC `S1234567A` is present in the system
        * The patient has no medicine usage entries
    * **Test Case:** `clearmu ic/S1234567A`
    * **Expected:** Klinix shows a message indicating that the patient with NRIC `S1234567A` has no medicine usage records to clear.  
      <br><br>

4. Clearing medicine usage records for a patient with no existing usages (by index)
    * **Prerequisite:**
        * The patient at index `1` has no medicine usage entries
    * **Test Case:** `clearmu 1`
    * **Expected:** Klinix shows a message indicating that the patient at index `1` has no medicine usage records to clear.  
      <br><br>

5. Clearing medicine usage records with an invalid NRIC format
    * **Prerequisites:**
        * None
    * **Test Case:** `clearmu ic/INVALID123`
    * **Expected:** Klinix shows an error indicating that the provided NRIC is not in a valid format.  
      <br><br>

6. Clearing medicine usage records with missing arguments
    * **Prerequisites:**
        * None
    * **Test Case:** `clearmu`
    * **Expected:** Klinix shows an error indicating that the input command is invalid.  
      <br><br>

7. Clearing medicine usage records using both index and NRIC
    * **Prerequisite:**
        * Any patients in the system
    * **Test Case:** `clearmu 2 ic/S1234567A`
    * **Expected:** Klinix shows an error indicating that the input command is invalid.  
      <br><br>

8. Clearing medicine usage records using index with extra text
    * **Prerequisite:**
        * Patient at index `1` exists
    * **Test Case:** `clearmu 1 extra`
    * **Expected:** Klinix shows an error indicating that the input command is invalid.  
      <br><br>

9. Clearing medicine usage records using a malformed input string
    * **Prerequisites:**
        * None
    * **Test Case:** `clearmu extra ic/S1234567A`
    * **Expected:** Klinix shows an error indicating that the input command is invalid.  
      <br><br>

### Finding Medicine Usage Records

**Command:** `findmu`<br>

1. Finding patients with keywords that overlap in meaning but not in text
    * **Prerequisite:**
        * No patients have medicine usage records that include the keyword `painkiller`, but some include `Paracetamol` or `Ibuprofen`
    * **Test Case:** `findmu painkiller`
    * **Expected:** Klinix shows no results since the system only supports literal (not semantic) matching.  
      <br><br>

2. Finding patients who have used specified medicines
    * **Prerequisite:**
        * Any state of the system
    * **Test Case:** `findmu paracetamol amoxicillin`
    * **Expected:** Klinix filters the patient list and shows all patients who have at least one medicine usage record matching either `paracetamol` or `amoxicillin`.  
      <br><br>

3. Finding patients with keywords that match no patient
    * **Prerequisite:**
        * No patient in the system has medicine usage containing the keyword `paracetamol`
    * **Test Case:** `findmu paracetamol`
    * **Expected:** Klinix updates the patient list to show no results since no medicine usage record matches the keyword.  
      <br><br>

4. Finding patients with an empty keyword input
    * **Prerequisite:**
        * Any state of the system
    * **Test Case:** `findmu`
    * **Expected:** Klinix shows an error indicating that the input command is invalid.  
      <br><br>

5. Finding patients with keywords containing extra spaces
    * **Prerequisite:**
        * Any state of the system
    * **Test Case:** `findmu paracetamol amoxicillin // Some white spaces in between but collapsed by markdown` 
    * **Expected:** Klinix trims extra spaces and performs the search as if the user entered: `paracetamol amoxicillin`.  
      <br><br>

6. Finding patients using case-insensitive keywords
    * **Prerequisite:**
        * A patient has `Amoxicillin` recorded in their medicine usage
    * **Test Case:** `findmu AMOXICILLIN`
    * **Expected:** Klinix performs a case-insensitive match and returns all patients with medicine names containing amoxicillin, ignoring case.  
      <br><br>

7. Finding patients using partial keyword 1
    * **Prerequisite:**
        * Any state of the system
    * **Test Case:** `findmu para`
    * **Expected:** Klinix shows all patients with medicine usage entries where the name partially matches `para`, such as `Paracetamol`.  
      <br><br>

8. Finding patients using partial keyword 2
    * **Prerequisite:**
        * A patient has `Amoxicillin` recorded in their medicine usage
    * **Test Case:** `findmu cill`
    * **Expected:** Klinix shows all patients with medicine usage entries containing `cill` anywhere in the medicine name, such as `Amoxicillin`.  
      <br><br>

9. Finding patients with multiple partial keywords
    * **Prerequisite:**
        * A patient has `Paracetamol` and another has `Amoxicillin`
    * **Test Case:** `findmu cetam oxicil`
    * **Expected:** Klinix returns patients with medicine names partially matching `cetam` and `oxicil`, such as `Paracetamol` and `Amoxicillin`.  
      <br><br>

### Adding an Appointment

**Command:** `addappt`<br>

1. Adding an appointment to a patient
    * **Prerequisites:**
        * The patient with that NRIC must be present in the patient list
        * The appointment the user adds must not overlap with any existing appointment of that patient
          <br><br>
    * **Test Case:** `addappt ic/S1234567A appt/Check-Up from/22-02-2025 11:00 to/22-02-2025 11:30`
    * **Expected:** The patient with NRIC S123457A in the list is updated with the following fields:
        * Appointment: `Check-Up FROM 22-02-2025 11:00 TO 22-02-2025 11:30`
        * Other fields remain unchanged
          <br><br>
2. Adding an appointment that overlaps with existing patient's appointments
    * **Prerequisites:**
        * The patient with that NRIC must be present in the patient list
        * The appointment the user adds must overlap with an existing appointment
          <br><br>
    * **Test Case:** `addappt ic/S1234567A appt/Injection from/22-02-2025 11:15 to/22-02-2025 11:45`
    * **Expected:** Klinix throws an error message showing the details of overlapping appointments.
      <br><br>

### Deleting an Appointment

**Command:** `deleteappt`<br>

1. Deleting an appointment from a patient who has an appointment
    * **Prerequisites:**
        * The patient with that NRIC must be present in the patient list
        * That patient has existing appointment(s)
          <br><br>
    * **Test Case:** `deleteappt 1 ic/S1234567A`
    * **Expected:** The patient with NRIC S1234567A in the list is updated with the following fields:
        * First appointment from patient is deleted.
        * Other fields remain unchanged.
        * Klinix will return a confirmation message showing the details of the deleted appointment in the message.
          <br><br>
2. Deleting an appointment from a patient who has no appointments
    * **Prerequisites:**
        * The patient with that NRIC must be present in the patient list
        * That patient has no existing appointment
          <br><br>
    * **Test Case:** `deleteappt 1 ic/S1234567A`
    * **Expected:** Klinix throws the error message indicating that the patient does not have any appointments.
      <br><br>

### Clearing Appointments

**Command:** `clearappt`<br>

**Format 1: Clear by NRIC**

1. Clearing appointments from a patient who has existing appointment(s)
   * **Prerequisites:**
     * The patient with that NRIC must be present in the patient list
     * That patient has existing appointment(s)
     <br><br>
   * **Test Case:** `clearappt ic/S1234567A`
   * **Expected:** The patient with NRIC S1234567A in the list is updated with the following fields:
       * All existing appointments cleared.
       * Other fields remain unchanged.
       * Klinix will return a confirmation message indicating appointments have been cleared.
         <br><br>
2. Clearing appointments from a patient who has no appointments
    * **Prerequisites:**
        * The patient with that NRIC must be present in the patient list
        * That patient has no existing appointment
          <br><br>
    * **Test Case:** `clearappt ic/S1234567A`
    * **Expected:** Klinix throws the error message indicating that the patient does not have any appointments.

**Format 2: Clear by index**

1. Clearing appointments from a patient who has existing appointment(s)
    * **Prerequisites:**
        * The patient with that index must be present in the patient list
        * That patient has existing appointment(s)
          <br><br>
    * **Test Case:** `clearappt 1`
    * **Expected:** The first patient in the list is updated with the following fields:
        * ALl existing appointments cleared.
        * Other fields remain unchanged.
        * Klinix will return a confirmation message indicating appointments have been cleared.
          <br><br>
2. Clearing appointments from a patient who has no appointments
    * **Prerequisites:**
        * The patient with that index must be present in the patient list
        * That patient has no existing appointment
          <br><br>
    * **Test Case:** `clearappt 1`
    * **Expected:** Klinix throws the error message indicating that the patient does not have any appointments.

### Marking an Appointment
**Command:** `markappt`<br>
1. Marking an appointment as visited
    * **Prerequisites:**
        * The patient with that NRIC must be present in the patient list
        * That patient has existing appointment(s)
          <br><br>
    * **Test Case:** `markappt 1 ic/S1234567A`
    * **Expected:** The first appointment of the patient with NRIC S1234567A in the list is updated with the following fields:
        * Appointment marked as visited.
        * Other fields remain unchanged.
        * Klinix will return a confirmation message showing the details of the marked appointment in the message.
          <br><br>
2. Marking an appointment that is already marked as visited
    * **Prerequisites:**
        * The patient with that NRIC must be present in the patient list
        * That patient has existing appointment(s)
          <br><br>
    * **Test Case:** `markappt 1 ic/S1234567A`
    * **Expected:** Klinix throws the error message indicating that the appointment is already marked as visited.
      <br><br>
3. Marking an appointment where the index does not exist
    * **Prerequisites:**
        * The patient with that NRIC must be present in the patient list
        * That patient has existing appointment(s)
          <br><br>
    * **Test Case:** `markappt 99 ic/S1234567A`
    * **Expected:** Klinix throws the error message indicating that the index is invalid.
      <br><br>
4. Marking an appointment with an invalid NRIC format
    * **Prerequisites:**
        * The patient with that NRIC must be present in the patient list
        * That patient has existing appointment(s)
          <br><br>
    * **Test Case:** `markappt 1 ic/1234567A`
    * **Expected:** Klinix throws the error message indicating that the NRIC is invalid.
      <br><br>

### Unmarking an Appointment
**Command:** `unmarkappt`<br>
1. Unmarking an appointment as not visited
    * **Prerequisites:**
        * The patient with that NRIC must be present in the patient list
        * That patient has existing appointment(s)
          <br><br>
    * **Test Case:** `unmarkappt 1 ic/S1234567A`
    * **Expected:** The first appointment of the patient with NRIC S1234567A in the list is updated with the following fields:
        * Appointment marked as not visited.
        * Other fields remain unchanged.
        * Klinix will return a confirmation message showing the details of the unmarked appointment in the message.
          <br><br>
2. Unmarking an appointment that is already marked as not visited
    * **Prerequisites:**
        * The patient with that NRIC must be present in the patient list
        * That patient has existing appointment(s)
          <br><br>
    * **Test Case:** `unmarkappt 1 ic/S1234567A`
    * **Expected:** Klinix throws the error message indicating that the appointment is already marked as not visited.
      <br><br>
3. Unmarking an appointment where the index does not exist
    * **Prerequisites:**
        * The patient with that NRIC must be present in the patient list
        * That patient has existing appointment(s)
          <br><br>
    * **Test Case:** `unmarkappt 99 ic/S1234567A`
    * **Expected:** Klinix throws the error message indicating that the index is invalid.
      <br><br>
4. Unmarking an appointment with an invalid NRIC format
    * **Prerequisites:**
        * The patient with that NRIC must be present in the patient list
        * That patient has existing appointment(s)
          <br><br>
    * **Test Case:** `unmarkappt 1 ic/1234567A`
    * **Expected:** Klinix throws the error message indicating that the NRIC is invalid.
      <br><br>

### Viewing Appointments Starting on A Specific Date
**Command:** `appton`<br>
1. Viewing appointments starting on a specific date
    * **Prerequisites:**
        * Appointments starting on date `22-02-2025` exist in the system
    * **Test Case:** `appton 22-02-2025`
    * **Expected:** The patient with NRIC S1234567A in the list is updated with the following fields:
        * All appointments starting on `22-02-2025` are shown.
        * Other fields remain unchanged.
        * Klinix will return a confirmation message showing the details of the appointment in the message.
          <br><br>
      
2. Viewing appointments starting on a date with no appointments
    * **Prerequisite:**
        * Appointments starting on date `22-02-2025` does not exist in the system
    * **Test Case:** `appton 22-02-2025`
    * **Expected:** Klinix throws the error message indicating that no appointments exist on that date.
      <br><br>

3. Viewing appointments starting on a date with an invalid format
    * **Prerequisite:**
        * Appointments starting on date `22-02-2025` exist in the system
    * **Test Case:** `appton 2025-02-22`
    * **Expected:** Klinix throws the error message indicating that the date format is invalid.
      <br><br>
--------------------------------------------------------------------------------------------------------------------
## **Planned Enhancements**
1. Enhanced Search Functionality<br>
Currently, the `find` command only supports searching by patient names. In future versions, we plan to extend this functionality 
to include other fields such as NRIC, phone number, or email address. This is to accommodate scenarios where multiple patients may 
share the same or similar names, and users may prefer searching by unique identifiers.

2. Implementation of `Edit Appointment` Feature<br>
We plan to implement an edit appointment feature to allow users to modify existing appointment details such as date, time, or
description. This provides greater flexibility and reflects real-world usage where appointment changes are common.

3. Improved Time Format for User Interface Display Inputs<br>
To enhance user-friendliness, future versions of the application should feature a more intuitive date-time display format.
Instead of showing time in the 24-hour format (e.g., 13:00), the system will display it in a 12-hour format with AM/PM notation
(e.g., 1:00 PM). This change aims to improve readability and align with common user preferences, particularly for those who may 
not be familiar with the 24-hour clock. It will also help reduce confusion when viewing or managing appointment times in the user interface.

4. Currently, the `addmr` command only allows adding a medical report to a patient by NRIC. We plan to implement a feature that extends this
functionality to allow adding a medical report by index as well. This will provide users with more flexibility in how they add medical reports.

5. Currently, the `appton` command only allows viewing appointments by start date. We plan to implement a feature that extends this functionality to allow
viewing appointments by a range of dates. This will provide users with more flexibility in how they view appointments.

6. Currently, Klinix only allow users to mark appointments as visited or not visited. We plan to implement a feature that extends this functionality to allow users to
mark appointments with more statuses such as canceled, or rescheduled. This will provide users with more flexibility in how they manage appointments.
--------------------------------------------------------------------------------------------------------------------

## **Appendix: Efforts**
#### Difficulty Level
Our project introduced a greater level of complexity compared to AB3, as it involved managing multiple entity types—primarily
appointments, medical reports, and medicine usages—whereas AB3 focused on a single entity. This expansion significantly increased the demands on command
processing, as each new entity came with its own set of attributes and behaviors that had to be handled appropriately.
#### Challenges Faced
**Integration of Person, Medical Report, Medicine Usage and Appointment Entities:** To accurately associate each appointment,
medical report and medicine usages with their corresponding patient, we added an `AppointmentList` and `MedicalReport` with `MedicalUsageList`
attributes to the Person class.

**Command Implementation:** Designing commands for both entities required thoughtful planning to ensure they operated correctly and 
consistently across different use cases.

**UI Space Constraints:** Creating a user-friendly interface within the constraints of limited screen space proved to be a major challenge.
We had to strike a balance between presenting enough information and keeping the layout clean and intuitive. Through multiple design iterations, we arrived at a solution that delivers key details without overwhelming the user.


#### Effort Required
**Design and Refactoring:** Adapting the AB3 framework to support multiple entity types required careful refactoring and the creation of new class
structures.

**Command Implementation:** We developed dedicated commands for `Person`, `Appointment`, `MedicalReport` and `MedicalUsage` 
which involved building additional parser classes and command logic.

**Testing and Debugging:** To ensure the reliability of our system, we wrote comprehensive test cases to verify that each 
feature and command functioned correctly across both entity types.

#### Achievements
In conclusion, our team successfully designed and implemented key features, resolved bugs, and navigated potential integration challenges. While we initially encountered difficulties with more complex components such as appointment and medicine usage management, effective collaboration allowed us to overcome these hurdles and accomplish our objectives for Klinix.



