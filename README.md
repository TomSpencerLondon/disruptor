## **Disruptor-Based Concurrent Messaging App**

### **Overview**
This application demonstrates how to use the **LMAX Disruptor** library for efficient, high-throughput, and low-latency message processing. It consists of two Spring Boot services:

1. **Client Service**: A web application with a Thymeleaf frontend where users can submit messages.
2. **Disruptor Service**: A backend service that uses the **Disruptor API** to process messages concurrently.

### **Key Features**
- **High-Throughput Message Processing**: Uses the Disruptor to handle concurrent message submissions efficiently.
- **Concurrent Request Simulation**: Demonstrates how the Disruptor manages concurrent requests with a simple integration test.
- **Clear Frontend**: A user-friendly form built with Thymeleaf and styled using Tailwind CSS.
- **Backend Integration**: Demonstrates how the Client Service sends messages to the Disruptor Service for processing.

---

### **What is the Disruptor API?**
The **Disruptor API** is a high-performance inter-thread messaging library developed by LMAX Group. It uses a **ring buffer** to manage messages efficiently in memory. Key benefits include:
1. **Low Latency**: Minimizes contention by avoiding traditional queues and locks.
2. **High Throughput**: Optimized for handling a large number of messages concurrently.
3. **Deterministic Performance**: Ensures predictable behavior, making it ideal for systems requiring high reliability and performance.

In this app, the Disruptor handles incoming messages by:
1. Placing them into a **ring buffer**.
2. Dispatching them to **event handlers** for processing in a concurrent manner.

---

### **How the App Works**
1. A user submits a message via the **Client Service**'s web form.
2. The Client Service sends the message to the **Disruptor Service**'s API.
3. The Disruptor Service:
    - Receives the message.
    - Places it into a ring buffer (managed by the Disruptor).
    - Processes the message using an event handler.
4. Logs in the Disruptor Service confirm that the message has been processed.

---

### **Project Structure**
```
/disruptor-thymeleaf-app
    ├── disruptor-service
    │   ├── src/main/java/com/example/disruptorservice
    │   └── src/main/resources
    ├── client-service
    │   ├── src/main/java/com/example/clientservice
    │   └── src/main/resources
    └── pom.xml
```

### **Disruptor Service Components**
- **MessageController**: Receives messages via REST API and places them into the Disruptor's ring buffer.
- **DisruptorConfig**: Configures the Disruptor, including its buffer size and event handlers.
- **MessageEvent**: Represents a single message in the Disruptor.
- **MessageEventHandler**: Processes messages from the Disruptor's ring buffer.

### **Client Service Components**
- **ClientController**: Handles user requests, sends messages to the Disruptor Service, and displays responses.
- **Frontend (Thymeleaf)**: A form-based UI for message submission.

---

### **How the Disruptor Helps with Concurrency**
The **Disruptor API** eliminates contention points that arise in traditional queue-based systems:
- **Lock-Free Design**: The Disruptor uses a lock-free algorithm to manage its ring buffer, minimizing overhead from context switching.
- **Memory Efficiency**: The ring buffer reuses pre-allocated memory, reducing garbage collection pressure.
- **Batch Processing**: The Disruptor efficiently processes multiple events in batches, reducing the latency of individual operations.
- **Thread Coordination**: It leverages **mechanical sympathy** by working with the CPU’s architecture for optimal thread coordination.

Compared to traditional queue-based systems:
- **Queues** involve locks and context switching, which slow down message processing.
- The Disruptor avoids these pitfalls, enabling high-performance concurrent processing.

---

### **Usage Instructions**

#### **Step 1: Clone the Repository**
```bash
git clone https://github.com/TomSpencerLondon/disruptor.git
cd disruptor
```

#### **Step 2: Run the Disruptor Service**
```bash
cd disruptor-service
mvn spring-boot:run
```

#### **Step 3: Run the Client Service**
```bash
cd ../client-service
mvn spring-boot:run
```

#### **Step 4: Access the Application**
1. Open the Client Service in your browser:
    - [http://localhost:8081](http://localhost:8081)
2. Submit a message using the form.

#### **Step 5: Observe the Logs**
- Disruptor Service logs will show:
    - Messages being placed in the ring buffer.
    - Messages being processed by the event handler.

---

### **Testing Concurrency**
An integration test simulates concurrent users sending 100 messages simultaneously to the Disruptor Service. It verifies:
1. **High Throughput**: Measures the time taken to process all messages.
2. **Correctness**: Ensures all messages are processed successfully.

To run the test:
```bash
mvn test -pl client-service
```

---

### **Sample Logs**
**Client Service**:
```
INFO  ClientController - Received message in Client Service: Hello Disruptor!
INFO  ClientController - Received response from Disruptor Service: Message published: Hello Disruptor!
```

**Disruptor Service**:
```
INFO  MessageController - Received message: Hello Disruptor!
INFO  MessageController - Message placed in ring buffer at sequence: 42
INFO  MessageEventHandler - Processing message: Hello Disruptor! at sequence: 42
```

---

## **Performance Testing and Why Disruptor Is Useful**

### **Testing Overview**
To evaluate the performance of the **Disruptor**, we conducted a series of tests comparing it to a traditional **BlockingQueue**. These tests were designed to measure:

1. **Processing Time**: How long it takes to process a fixed number of messages.
2. **Throughput**: The number of messages processed per second.
3. **Scalability**: How the system performs under high workloads and concurrency levels.

---

### **Test Setup**
We simulated a system where producers publish messages to a queue or ring buffer, and consumers process those messages. The tests included:

1. **Disruptor**:
    - A ring buffer configured with varying buffer sizes.
    - Lock-free design for high concurrency.
    - Tested buffer sizes: `32_768`, `262_144`, and `524_288`.

2. **BlockingQueue**:
    - A `LinkedBlockingQueue` as the traditional alternative.
    - Synchronized access leading to potential contention under high concurrency.

---

### **Key Results**
| **Metric**                | **Disruptor (262,144 buffer)**   | **BlockingQueue**             |
|---------------------------|----------------------------------|--------------------------------|
| Processing Time (1M msgs) | 1230 ms                         | 1416 ms                       |
| Throughput                | 813,008 messages/sec            | 706,214 messages/sec          |

#### **Observations**:
- The **Disruptor** outperformed the BlockingQueue by 15% in terms of throughput at 1 million messages.
- Larger buffer sizes initially improved performance, but beyond **262,144**, the Disruptor’s performance plateaued, suggesting hardware limits (e.g., CPU cache size or memory bandwidth).

---

### **Why the Disruptor Is Useful**

1. **High Throughput**:
    - The Disruptor’s lock-free design avoids contention, making it ideal for systems processing millions of messages per second.

2. **Low Latency**:
    - It minimizes delays caused by locks or context switching, ensuring consistent performance for time-sensitive applications.

3. **Scalability**:
    - The Disruptor excels under high concurrency and large workloads, as demonstrated in tests with 1 million messages.

4. **Efficiency**:
    - By leveraging CPU caches and avoiding traditional queues, the Disruptor makes optimal use of hardware resources.

5. **Ideal for Real-Time Systems**:
    - Applications requiring deterministic performance, such as financial systems or telemetry, benefit significantly from the Disruptor.

---

### **Conclusion**
Testing demonstrates that the **Disruptor** is a powerful tool for high-throughput, low-latency systems. While the **BlockingQueue** performs well for moderate workloads, the Disruptor shines in high-concurrency scenarios, making it a better choice for applications with demanding performance requirements.

Let me know if you’d like further refinements or additional details to include! 🚀

### **Key Takeaways**
- The **Disruptor API** is a powerful tool for concurrent message processing, eliminating bottlenecks in traditional queue-based systems.
- This app showcases a simple but effective use case, demonstrating high throughput and efficient concurrency handling.
