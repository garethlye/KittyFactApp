# Android Coding Challenge

## Table of Contents:

1. [Introduction](#introduction)
2. [Objective](#objective)
3. [Submission Instructions](#submission-instructions)
4. [To Do Tasks](#to-do-tasks)
5. [Technical Requirements](#technical-requirements)
6. [Evaluation Criteria](#evaluation-criteria)

## Introduction

Welcome! We're excited to see your skills in action. This challenge is designed to assess your proficiency in Android development, focusing on core competencies relevant to a Senior/Lead Android Engineer role.

**Estimated Time Commitment:** Approximately **6-8 hours**.

**Deadline:** We do not have a deadline, so please feel free to work on it at your own pace. However, we kindly ask that you provide an estimated time for submission. If you require additional time, simply let us know the updated estimate.

---

## Objective

Enhance a simple Android application using Kotlin that displays cat facts by implementing specific features and demonstrating best practices in Android development.

Please read the instructions carefully and ensure they are followed as outlined. 

---

## Submission Instructions

1. **Create the Repository**  
   * To create the challenge repository: [**GitHub Repository Link**](https://github.com/apparray/speakbuddy_android_challenge) under your GitHub account, please
     * Click **Use this template** button on the top right of this page
     * Click **Create a new repository** item from the drop-down list
     * Create a new repository under your own GitHub account
2. **Implement Your Solution**  
   * Clone your new repository  
   * Create a new branch  
   * Implement the required features.
   * Commit changes with clear messages:  
3. **Push and Create a Pull Request**  
   * Push your branch  
   * On GitHub, create a pull request from your branch to **your `main` branch in your own repository**.
   * **Title:** `Android Challenge Submission - Your Name`  
   * **Description:** Include an overview of your implementation and any important notes. ([See Evaluation criteria #6. Documentation for more items](#evaluation-criteria))  
4. **Submit Your PR Link**  
   * Ensure your repository is public.  
   * Send the link to your pull request to our talent acquisition team.

---

## To Do Tasks

### Core Requirements

1. **Persist Latest Cat Fact**  
   * **Goal:** Save the latest cat fact locally so it persists between app launches.  
   * **Implementation:** Use either **Room** or **DataStore** for data persistence.  
   * **Behavior:** Upon relaunching the app, the last fetched cat fact should be displayed without fetching from the network.  
2. **Display Fact Length**  
   * **Goal:** Show the character length of a cat fact when it exceeds 100 characters.  
   * **Implementation:** Use the `length` field from the API response.  
   * **Behavior:** If a fact's length is greater than 100, display the length on the Home screen at the position that you feel fit.  
3. **Indicate Multiple Cats**  
   * **Goal:** Inform the user when a fact contains the word **"cats"**.  
   * **Implementation:** Perform a simple text search for the word "cats" in the fact.  
   * **Behavior:** If the fact contains "cats", prominently display the text **"Multiple cats!"** on the Home screen.  
4. **Decorate Home screen**  
   * **Goal**: Add image(s) on the home screen  
   * **Implementation**: Perform Image download over network using Coil (you can use any image readily available on the internet)  
   * **Behavior**: You can show any image on the home screen that you think makes the screen more appealing to the users.
5. **Unit Testing**  
   * **Goal:** Ensure the reliability of your code through testing.
   * **Implementation:** Write unit tests for the **ViewModel** and **business logic** layers using `fake` or `mockk`.  
   * **Requirements:** Tests should cover happy paths and critical scenarios.

---

## Technical Requirements

* **Language:** Kotlin  
* **Architecture:** MVVM  
* **Libraries & Tools:**  
  * **Kotlin Coroutines** & **Flow**  
  * **Jetpack Compose** for UI  
    * If you don’t have experience, please challenge yourself!  
  * **Hilt** for dependency injection  
  * **Room** or **DataStore** for local data persistence  
  * **Coil** for Image downloader  
* **Minimum SDK Version:** 26  
* **Target SDK Version:** Latest stable version

---

## Evaluation Criteria

Your submission will be evaluated based on:

1. **Functionality**  
   * Correct implementation of all core requirements without any bugs.  
   * App runs smoothly without crashes.  
2. **Code Quality**  
   * Clean, readable, and maintainable code (use SOLID and DRY principle).  
   * Adherence to Android and Kotlin coding standards and best practices.  
3. **Architecture & Design**  
   * Proper implementation of the MVVM pattern.  
   * Clear separation of concerns between layers.  
   * Effective use of dependency injection with Hilt.  
4. **Testing**  
   * Implement unit tests for ViewModel and business logic layers.  
   * Tests should cover happy paths and critical scenarios.  
5. **Error Handling**  
   * Graceful handling of errors and edge cases.  
   * No unhandled exceptions or app crashes.  
6. **Documentation**  
   * Clear code comments where necessary.  
   * Implement Jetpack Compose UI Preview  
   * PR Description should include below item (not limited to):  
     1. Brief explanations of architectural decisions  
     2. Complex logic  
     3. Screenshoot or video evidence of the core requirements  
     4. Anything you want to explain to the reviewer  
7. **Submission Compliance**  
   * Followed submission instructions precisely.  
   * Code compiles and runs without additional setup.

---

#### We Look Forward to Your Submission!

Thank you for taking the time to complete this challenge. We look forward to reviewing your work and discussing it with you.

---

#### Good Luck!

If you have any questions, please don't hesitate to reach out.

