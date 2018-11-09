import React from 'react';
import $ from 'jquery';
import { Form, Icon, Input, Button, message } from 'antd';
import { API_ROOT } from '../constants';
import PropTypes from 'prop-types';
import {Log} from './Log';
import {LogData} from './LogData'
import {ResponseLogData} from './ResponseLogData'
const FormItem = Form.Item;


class AddStopForm extends React.Component {
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
                var temp = {number: d.length, content:'add stop,'+ values.StopID + ',' + values.StopName + ',' + values.lat + ',' + values.lon};
                d.push(temp);
                LogData.setData(d);
                var parent = this;
                this.props.transferMsg(d);
                $.ajax({
                    url: `${API_ROOT}/command`,
                    method: 'POST',
                    data: 'add_stop,'+ values.StopID + ',' + values.StopName + ',' + values.lat + ',' + values.lon,

                }).then((response) => {
                    message.success("stop added");
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
    handleConfirmBlur = (e) => {
        const value = e.target.value;
        this.setState({ confirmDirty: this.state.confirmDirty || !!value });
    }
    checkXCoordinate = (rule, value, callback) => {
        const form = this.props.form;
        if (value && !(value <= 90 && value >= -90)) {
            callback('Invalid Coordinate');
        } else {
            callback();
        }
    }
    checkNum = (rule, value, callback) => {
        const form = this.props.form;
        if (value && !(value < Number.MAX_SAFE_INTEGER && value >= 0 )) {
            callback('Invalid Number');
        } else {
            callback();
        }
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
                    <Button type="primary" htmlType="submit">Add Stop</Button>
                </FormItem>

                <FormItem
                    {...formItemLayout}
                    label="Stop ID"
                >
                    {getFieldDecorator('StopID', {
                        rules: [{ required: true, message: 'Please input Stop ID!', whitespace: true }, {
                            validator: this.checkNum,
                        }],
                    })(
                        <Input />
                    )}
                </FormItem>

                <FormItem
                    {...formItemLayout}
                    label="Stop Name"
                >
                    {getFieldDecorator('StopName', {
                        rules: [{ required: true, message: 'Please input Stop Name!', whitespace: true }],
                    })(
                        <Input />
                    )}
                </FormItem>

                <FormItem
                    {...formItemLayout}
                    label="X-coord"
                >
                    {getFieldDecorator('lat', {
                        rules: [{ required: true, message: 'Please input X coordinate!', whitespace: true }, {
                            validator: this.checkXCoordinate,
                        }],
                    })(
                        <Input />
                    )}
                </FormItem>

                <FormItem
                    {...formItemLayout}
                    label="Y-coord"
                >
                    {getFieldDecorator('lon', {
                        rules: [{ required: true, message: 'Please input Y coordinate!', whitespace: true }, {
                            validator: this.checkYCoordinate,
                        }],
                    })(
                        <Input />
                    )}

                    <p>{"ID <= 1000000: Bus Stop \n" + "ID > 1000000: Rail Station"}</p>
                </FormItem>

            </Form>
        );
    }
}


export const AddStop = Form.create()(AddStopForm);