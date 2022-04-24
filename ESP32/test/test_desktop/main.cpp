#include <unity.h>

void test_humidity(void);
void test_pump(void);

int main(int argc, char **argv) {
    UNITY_BEGIN();
    RUN_TEST(test_pump);
    RUN_TEST(test_humidity);
    UNITY_END();
    return 0;
}