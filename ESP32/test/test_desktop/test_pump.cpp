//
// Created by michael on 4/6/22.
//

#include <pump.h>
#include <unity.h>

void test_function(void) {

}

int main(int argc, char **argv) {
    UNITY_BEGIN();
    RUN_TEST(test_function_fail);
    UNITY_END();
    return 0;
}