import React from 'react';
import $ from 'jquery';
import { Form, Icon, Input, Button, message } from 'antd';
import { API_ROOT } from '../constants';
import PropTypes from 'prop-types';
import {Log} from './Log';
import {LogData} from './LogData'
import {ResponseLogData} from './ResponseLogData'
const FormItem = Form.Item;


class MultiSimForm extends React.Component {
    state = {
        confirmDirty: false,
        autoCompleteResult: [],
    };
    handleSubmit = (e) => {
        e.preventDefault();
        this.props.form.validateFieldsAndScroll((err, values) => {
            if (!err) {
                console.log('Received values of form: ', values);

                var d = LogData.getData();
                var rld = ResponseLogData.getData();
                var temp = {number: d.length, content:'step_multi,'+ values.repetition + (typeof values.option1 != "undefined" ? "," + values.option1 : "" ) + (typeof values.option2 != "undefined" ? "," + values.option2 : "" )};
                d.push(temp);
                LogData.setData(d);
                var parent = this;
                this.props.transferMsg(d);
                $.ajax({
                    url: `${API_ROOT}/command`,
                    method: 'POST',
                    data: 'step_multi,'+ values.repetition + (typeof values.option1 != "undefined" ? "," + values.option1 : "" ) + (typeof values.option2 != "undefined" ? "," + values.option2 : "" )

                }).then((response) => {
                    message.success("multiple step");
                    var tmp = {number: rld.length, content:response};
                    rld.push(tmp);
                    //this.props.transferMsg(d, rld).bind(window);
                    parent.props.transferMsg(d, rld);
                    }, (response) => {
                    message.error(response.responseText);
                }).catch((error) => {
                    message.error(error);
                });
            }
        });
    }
    checkNum = (rule, value, callback) => {
        const form = this.props.form;
        if (value && !(value < Number.MAX_SAFE_INTEGER && value >= 0 )) {
            callback('Invalid Number');
        } else {
            callback();
        }
    }
    checkPassword = (rule, value, callback) => {
        const form = this.props.form;
        // if (value && value !== form.getFieldValue('password')) {
        //     callback('Two passwords that you enter is inconsistent!');
        // } else {
        //     callback();
        // }
    }
    checkConfirm = (rule, value, callback) => {
        const form = this.props.form;
        if (value && this.state.confirmDirty) {
            form.validateFields(['confirm'], { force: true });
        }
        callback();
    }
    render() {
        const { getFieldDecorator } = this.props.form;
        const formItemLayout = {
            labelCol: {
                xs: { span: 24 },
                sm: { span: 8 },
            },
            wrapperCol: {
                xs: { span: 24 },
                sm: { span: 16 },
            },
        };
        const tailFormItemLayout = {
            wrapperCol: {
                xs: {
                    span: 24,
                    offset: 0,
                },
                sm: {
                    span: 16,
                    offset: 8,
                },
            },
        };
        return (
            <Form onSubmit={this.handleSubmit} className="multisim-form">
                <FormItem {...tailFormItemLayout}>
                    <Button type="primary" htmlType="submit">Multiple Step</Button>
                </FormItem>

                <FormItem
                    {...formItemLayout}
                    label="Repetition"
                >
                    {getFieldDecorator('repetition', {
                        rules: [{ required: true, message: 'Please input your repetition!', whitespace: true }, {
                            validator: this.checkNum,
                        }],
                    })(
                        <Input />
                    )}
                </FormItem>
                <FormItem
                    {...formItemLayout}
                    label="Option1"
                >
                    {getFieldDecorator('option1', {
                        rules: [{
                            required: false, message: 'Please input your optional parameters!',
                        }, {
                            validator: this.checkNum,
                        }],
                    })(
                        <Input />
                    )}
                </FormItem>
                <FormItem
                    {...formItemLayout}
                    label="Option2"
                >
                    {getFieldDecorator('option2', {
                        rules: [{
                            required: false, message: 'Please input your optional parameters!',
                        }, {
                            validator: this.checkNum,
                        }],
                    })(
                        <Input />
                    )}
                </FormItem>
            </Form>
        );
    }
}


export const MultiStep = Form.create()(MultiSimForm);